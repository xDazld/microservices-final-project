/*global console, window */

/* jshint esversion: 6, strict: false */

/**
 * Dashboard WebSocket Client
 *
 * Establishes a persistent connection to the server's WebSocket endpoint
 * and receives real-time updates for metrics, logs, and statistics.
 */

class DashboardWebSocketClient {
    constructor() {
        this.ws = null;
        this.messageHandlers = {};
        this.reconnectAttempts = 0;
        this.maxReconnectAttempts = 10;
        this.reconnectDelay = 3000; // 3 seconds
        this.subscriptions = new Set();
        this.isConnected = false;
    }

    /**
     * Get constants
     */
    static get RECONNECT_DELAY_MS() {
        return 3000;
    }

    static get MAX_RECONNECT_DELAY_MS() {
        return 30000;
    }

    static get PING_INTERVAL_MS() {
        return 30000;
    }

    static get MILLION() {
        return 1000000;
    }

    static get THOUSAND() {
        return 1000;
    }

    /**
     * Initialize WebSocket connection
     */
    connect() {
        const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
        const wsUrl = `${protocol}//${window.location.host}/ws/dashboard`;

        console.log('Connecting to dashboard WebSocket:', wsUrl);

        try {
            this.ws = new WebSocket(wsUrl);

            this.ws.onopen = () => {
                console.log('Dashboard WebSocket connected');
                this.isConnected = true;
                this.reconnectAttempts = 0;
                this.emit('connected', {message: 'Connected to event stream'});
            };

            this.ws.onmessage = (event) => {
                try {
                    const message = JSON.parse(event.data);
                    this.handleMessage(message);
                } catch (e) {
                    console.error('Failed to parse WebSocket message:', e);
                }
            };

            this.ws.onerror = (error) => {
                console.error('Dashboard WebSocket error:', error);
                this.isConnected = false;
                this.emit('error', {error: 'WebSocket error'});
            };

            this.ws.onclose = () => {
                console.log('Dashboard WebSocket closed');
                this.isConnected = false;
                this.emit('disconnected', {message: 'Disconnected from event stream'});
                this.attemptReconnect();
            };
        } catch (e) {
            console.error('Failed to create WebSocket:', e);
            this.attemptReconnect();
        }
    }

    /**
     * Handle incoming WebSocket messages
     */
    handleMessage(message) {
        const type = message.type;

        switch (type) {
            case 'CONNECTED':
                console.log('Server confirmation:', message.message);
                break;

            case 'METRICS_UPDATE':
                this.emit('metrics', message);
                this.updateMetricDisplay(message.metric, message.value);
                break;

            case 'QUERY_LOG':
                this.emit('log', message);
                this.updateLogsDisplay(message.log);
                break;

            case 'STATS_UPDATE':
                this.emit('stats', message);
                this.updateStatsDisplay(message.statsType, message.data);
                break;

            case 'SECURITY_ALERT':
                this.emit('alert', message);
                console.warn('Security Alert:', message.alertType, message.domain);
                break;

            case 'PONG':
                // Response to ping, connection is healthy
                break;

            case 'SUBSCRIBED':
                console.log('Subscribed to channel:', message.channel);
                break;

            case 'UNSUBSCRIBED':
                console.log('Unsubscribed from channel:', message.channel);
                break;

            case 'ERROR':
                console.error('Server error:', message.message);
                break;

            default:
                console.warn('Unknown message type:', type);
        }
    }

    /**
     * Update metric display in dashboard
     */
    updateMetricDisplay(metricName, value) {
        const formattedValue = this.formatNumber(value);

        switch (metricName) {
            case 'dns.query.count':
                const totalQueriesEl = document.getElementById('total-queries');
                if (totalQueriesEl) {
                    totalQueriesEl.textContent = formattedValue;
                }
                break;

            case 'dns.filter.checks':
                const blockedEl = document.getElementById('blocked-queries');
                if (blockedEl) {
                    blockedEl.textContent = formattedValue;
                }
                const metricFiltersEl = document.getElementById('metric-filters');
                if (metricFiltersEl) {
                    metricFiltersEl.textContent = formattedValue;
                }
                break;

            case 'dns.cache.hit.rate':
                const cacheRateEl = document.getElementById('metric-cache-rate');
                if (cacheRateEl) {
                    cacheRateEl.textContent = formattedValue;
                }
                break;
        }
    }

    /**
     * Update logs display in dashboard
     */
    updateLogsDisplay(logEntry) {
        // Update log statistics would be done via separate stats update messages
        // In practice, you'd fetch fresh stats from the server

        // Update the logs table if it's visible
        const logsContainer = document.getElementById('query-logs-container');
        if (logsContainer && logsContainer.innerHTML.includes('<table')) {
            this.prependLogEntry(logsContainer, logEntry);
        }
    }

    /**
     * Prepend a log entry to the logs table
     */
    prependLogEntry(container, logEntry) {
        const table = container.querySelector('table');
        if (!table) {
            return;
        }

        const tbody = table.querySelector('tbody');
        if (!tbody) {
            return;
        }

        const row = document.createElement('tr');
        row.innerHTML = `
            <td class="timestamp-cell">${this.escapeHtml(logEntry.timestamp || new Date().toISOString())}</td>
            <td class="domain-cell">${this.escapeHtml(logEntry.domain || '')}</td>
            <td>${this.escapeHtml(logEntry.queryType || 'A')}</td>
            <td><span class="status-badge status-${(logEntry.status || '').toLowerCase()}">${this.escapeHtml(logEntry.status || '')}</span></td>
            <td>${logEntry.rcode || 0}</td>
            <td class="answers-cell" title="${this.escapeHtml((logEntry.answers || []).join(', '))}">${this.escapeHtml((logEntry.answers || []).join(', ') || '--')}</td>
            <td>${this.escapeHtml(logEntry.sourceIp || '')}</td>
        `;

        // Insert at the beginning, limit to 100 rows
        tbody.insertBefore(row, tbody.firstChild);

        // Remove older rows if we exceed 100
        const rows = tbody.querySelectorAll('tr');
        if (rows.length > 100) {
            rows[rows.length - 1].remove();
        }
    }

    /**
     * Update statistics display
     */
    updateStatsDisplay(statsType, data) {
        const statsContainer = document.getElementById(`${statsType}-details`);
        const statsEl = document.getElementById(`${statsType}-stats`);

        // Generate stats HTML
        const html = Object.entries(data)
            .map(([key, value]) => `
                <div style="display: flex; justify-content: space-between; padding: 10px 0; border-bottom: 1px solid var(--border-color);">
                    <span style="color: var(--text-secondary);">${this.formatLabel(key)}</span>
                    <span style="font-weight: bold;">${value}</span>
                </div>
            `).join('');

        if (statsContainer) {
            statsContainer.innerHTML = html;
        }
        if (statsEl) {
            statsEl.innerHTML = html;
        }
    }

    /**
     * Register a message handler for a specific event type
     */
    on(type, callback) {
        if (!this.messageHandlers[type]) {
            this.messageHandlers[type] = [];
        }
        this.messageHandlers[type].push(callback);
    }

    /**
     * Emit an event to all registered handlers
     */
    emit(type, data) {
        if (this.messageHandlers[type]) {
            this.messageHandlers[type].forEach(callback => {
                try {
                    callback(data);
                } catch (e) {
                    console.error(`Error in ${type} handler:`, e);
                }
            });
        }
    }

    /**
     * Subscribe to a channel
     * @param {string} channel - Channel name to subscribe to
     * @public
     */
    subscribe(channel) {
        if (this.isConnected && this.ws.readyState === WebSocket.OPEN) {
            const message = {type: 'SUBSCRIBE', channel: channel};
            this.ws.send(JSON.stringify(message));
            this.subscriptions.add(channel);
        }
    }

    /**
     * Unsubscribe from a channel
     * @param {string} channel - Channel name to unsubscribe from
     * @public
     */
    unsubscribe(channel) {
        if (this.isConnected && this.ws.readyState === WebSocket.OPEN) {
            const message = {type: 'UNSUBSCRIBE', channel: channel};
            this.ws.send(JSON.stringify(message));
            this.subscriptions.delete(channel);
        }
    }

    /**
     * Send a ping to keep connection alive
     */
    ping() {
        if (this.isConnected && this.ws.readyState === WebSocket.OPEN) {
            this.ws.send(JSON.stringify({type: 'PING'}));
        }
    }

    /**
     * Attempt to reconnect with exponential backoff
     */
    attemptReconnect() {
        if (this.reconnectAttempts < this.maxReconnectAttempts) {
            this.reconnectAttempts++;
            const delay = this.reconnectDelay * Math.pow(2, this.reconnectAttempts - 1);
            console.log(`Attempting to reconnect in ${delay}ms (attempt ${this.reconnectAttempts}/${this.maxReconnectAttempts})`);

            setTimeout(() => {
                this.connect();
            }, Math.min(delay, DashboardWebSocketClient.MAX_RECONNECT_DELAY_MS));
        } else {
            console.error('Max reconnection attempts reached. Manual reload may be required.');
            this.emit('reconnect_failed', {message: 'Failed to reconnect after multiple attempts'});
        }
    }

    /**
     * Disconnect WebSocket
     */
    disconnect() {
        if (this.ws) {
            this.ws.close();
        }
    }

    /**
     * Format a number for display
     */
    formatNumber(num) {
        if (isNaN(num) || !isFinite(num)) {
            return '0';
        }
        if (num >= DashboardWebSocketClient.MILLION) {
            return (num / DashboardWebSocketClient.MILLION).toFixed(1) + 'M';
        }
        if (num >= DashboardWebSocketClient.THOUSAND) {
            return (num / DashboardWebSocketClient.THOUSAND).toFixed(1) + 'K';
        }
        return Math.round(num).toString();
    }

    /**
     * Format a label (camelCase to Title Case)
     */
    formatLabel(key) {
        return key
            .replace(/([A-Z])/g, ' $1')
            .replace(/^./, str => str.toUpperCase())
            .trim();
    }

    /**
     * Escape HTML to prevent XSS
     */
    escapeHtml(text) {
        if (!text) {
            return '';
        }
        const div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
    }
}

// Initialize WebSocket client on page load
let dashboardWs = null;

document.addEventListener('DOMContentLoaded', () => {
    // Only initialize if we're on a dashboard page
    if (document.querySelector('[hx-get="/ui/stats/queries"]') ||
        document.querySelector('[hx-get="/ui/logs/table"]')) {

        dashboardWs = new DashboardWebSocketClient();

        // Set up event handlers
        dashboardWs.on('connected', () => {
            console.log('Dashboard event stream ready');
        });

        dashboardWs.on('metrics', () => {
            // Real-time metric update received
        });

        dashboardWs.on('log', () => {
            // Real-time log entry received
        });

        dashboardWs.on('stats', () => {
            // Real-time stats update received
        });

        // Connect to WebSocket
        dashboardWs.connect();

        // Send periodic pings to keep connection alive
        setInterval(() => {
            if (dashboardWs && dashboardWs.isConnected) {
                dashboardWs.ping();
            }
        }, DashboardWebSocketClient.PING_INTERVAL_MS);
    }
});

// Cleanup on page unload
window.addEventListener('beforeunload', () => {
    if (dashboardWs) {
        dashboardWs.disconnect();
    }
});
