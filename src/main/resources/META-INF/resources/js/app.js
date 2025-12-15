/*global console, confirm, htmx, window */
/* jshint esversion: 11, strict: false */

// DNS Filtering Service - Frontend JavaScript

document.addEventListener('DOMContentLoaded', function () {
    initializeApp();
});

function initializeApp() {
    // Initialize HTMX event handlers
    initHtmxHandlers();

    // Update UI based on authentication status
    updateAuthUI();

    // Don't load data if we're on the login page
    const isLoginPage = window.location.pathname.includes('/login');

    if (!isLoginPage) {
        // Load initial data only if authenticated
        if (isAuthenticated()) {
            // HTMX handles all dashboard stats loading via hx-trigger="load"
            // No need to manually call loadDashboardStats() as it conflicts with HTMX
            void 0; // Explicit no-op
        } else {
            // Not authenticated and not on login page - redirect to login
            sessionStorage.setItem('redirect_after_login', window.location.pathname);
            window.location.href = '/ui/login';
        }
    }

    // Initialize entertainment features
    initEntertainmentFeatures();
}

// Update UI based on authentication status
function updateAuthUI() {
    const loginLink = document.getElementById('login-link');
    const logoutLink = document.getElementById('logout-link');

    if (isAuthenticated()) {
        if (loginLink) {
            loginLink.style.display = 'none';
        }
        if (logoutLink) {
            logoutLink.style.display = 'inline';
            const username = sessionStorage.getItem('username') || 'User';
            logoutLink.textContent = `Logout (${username})`;
        }
    } else {
        if (loginLink) {
            loginLink.style.display = 'inline';
        }
        if (logoutLink) {
            logoutLink.style.display = 'none';
        }
    }
}

// Get JWT token from sessionStorage
function getAuthToken() {
    return sessionStorage.getItem('jwt_token');
}

// Check if user is authenticated
function isAuthenticated() {
    return getAuthToken() !== null;
}

// Logout function
function logout() {
    sessionStorage.removeItem('jwt_token');
    sessionStorage.removeItem('username');
    sessionStorage.removeItem('role');
    window.location.href = '/ui/login';
}

// Helper function for authenticated fetch requests
async function authenticatedFetch(url, options = {}) {
    const token = getAuthToken();

    // Add Authorization header if token exists
    if (token) {
        options.headers = {
            ...options.headers,
            'Authorization': 'Bearer ' + token
        };
    }

    // Set default Content-Type if not provided and body exists
    if (options.body && !options.headers['Content-Type']) {
        options.headers['Content-Type'] = 'application/json';
    }

    const response = await fetch(url, options);

    // Handle 401 Unauthorized
    if (response.status === 401) {
        // Don't redirect if already on login page
        if (!window.location.pathname.includes('/login')) {
            sessionStorage.setItem('redirect_after_login', window.location.pathname);
            window.location.href = '/ui/login';
            throw new Error('Unauthorized - redirecting to login');
        }
    }

    return response;
}

function initHtmxHandlers() {
    // NOTE: JWT token injection and 401 handling are configured in layout.qute.html
    // to ensure they run before any hx-trigger="load" elements are processed

    // Handle HTMX before request - add loading state
    document.body.addEventListener('htmx:beforeRequest', function (event) {
        const target = event.target;
        if (target.classList.contains('btn')) {
            target.classList.add('loading');
        }
    });

    // Handle HTMX after request - remove loading state
    document.body.addEventListener('htmx:afterRequest', function (event) {
        const target = event.target;
        target.classList.remove('loading');
    });

    // Handle HTMX errors (non-401 errors)
    document.body.addEventListener('htmx:responseError', function (event) {
        // 401 errors are handled in layout.qute.html
        if (event.detail.xhr.status !== 401) {
            showAlert('Error: ' + event.detail.xhr.statusText, 'danger');
        }
    });
}

// Dashboard Stats
async function loadDashboardStats() {
    try {
        const response = await authenticatedFetch('/api/v1/admin/stats');

        if (response.ok) {
            const stats = await response.json();
            updateStatsDisplay(stats);
        }
    } catch (error) {
        console.error('Failed to load stats:', error);
    }
}

function updateStatsDisplay(stats) {
    // Update stat cards if they exist
    const totalQueriesEl = document.getElementById('total-queries');
    const cacheHitsEl = document.getElementById('cache-hits');
    const blockedEl = document.getElementById('blocked-queries');
    const threatsEl = document.getElementById('threats-detected');

    if (totalQueriesEl) {
        totalQueriesEl.textContent = formatNumber(stats.totalQueries || 0);
    }

    // Cache hits comes from positive cache active count
    if (cacheHitsEl && stats.cache && stats.cache.positiveCache) {
        cacheHitsEl.textContent = formatNumber(stats.cache.positiveCache.active || 0);
    }

    if (blockedEl) {
        blockedEl.textContent = formatNumber(stats.filterChecks || 0);
    }

    // Threats is the sum of malicious domains and IPs
    if (threatsEl && stats.security) {
        const domains = stats.security.maliciousDomains || 0;
        const ips = stats.security.maliciousIPs || 0;
        threatsEl.textContent = formatNumber(domains + ips);
    }
}

// Filter Rules Management
function openAddRuleModal() {
    const modal = document.getElementById('rule-modal');
    const form = document.getElementById('rule-form');
    const title = document.getElementById('modal-title');

    if (modal && form && title) {
        title.textContent = 'Add New Filter Rule';
        form.reset();
        form.dataset.mode = 'create';
        delete form.dataset.ruleId;
        modal.classList.add('active');
    }
}

function openEditRuleModal(ruleId) {
    const modal = document.getElementById('rule-modal');
    const form = document.getElementById('rule-form');
    const title = document.getElementById('modal-title');

    if (modal && form && title) {
        title.textContent = 'Edit Filter Rule';
        form.dataset.mode = 'edit';
        form.dataset.ruleId = ruleId;

        // Fetch current rule data
        authenticatedFetch(`/api/v1/filters/${ruleId}`)
            .then(res => res.json())
            .then(rule => {
                document.getElementById('rule-name').value = rule.name || '';
                document.getElementById('rule-pattern').value = rule.pattern || '';
                document.getElementById('rule-type').value = rule.type || 'BLOCK';
                document.getElementById('rule-category').value = rule.category || 'custom';
                document.getElementById('rule-priority').value = rule.priority || 50;
                modal.classList.add('active');
            })
            .catch(err => {
                showAlert('Failed to load rule: ' + err.message, 'danger');
            });
    }
}

function closeModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) {
        modal.classList.remove('active');
    }
}

async function saveRule(event) {
    event.preventDefault();

    const form = document.getElementById('rule-form');
    const mode = form.dataset.mode;
    const ruleId = form.dataset.ruleId;

    const ruleData = {
        name: document.getElementById('rule-name').value,
        pattern: document.getElementById('rule-pattern').value,
        type: document.getElementById('rule-type').value,
        category: document.getElementById('rule-category').value,
        priority: parseInt(document.getElementById('rule-priority').value)
    };

    try {
        const url = mode === 'edit' ? `/api/v1/filters/${ruleId}` : '/api/v1/filters';
        const method = mode === 'edit' ? 'PUT' : 'POST';

        const response = await authenticatedFetch(url, {
            method: method,
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(ruleData)
        });

        if (response.ok) {
            showAlert(`Rule ${mode === 'edit' ? 'updated' : 'created'} successfully!`, 'success');
            closeModal('rule-modal');
            // Refresh rules list
            htmx.trigger('#rules-table-body', 'refreshRules');
        } else {
            const error = await response.json();
            showAlert('Error: ' + (error.error || 'Unknown error'), 'danger');
        }
    } catch (error) {
        showAlert('Error: ' + error.message, 'danger');
    }
}

async function deleteRule(ruleId, ruleName) {
    if (!confirm(`Are you sure you want to delete the rule "${ruleName}"?`)) {
        return;
    }

    try {
        const response = await authenticatedFetch(`/api/v1/filters/${ruleId}`, {
            method: 'DELETE'
        });

        if (response.ok) {
            showAlert('Rule deleted successfully!', 'success');
            // Refresh rules list
            htmx.trigger('#rules-table-body', 'refreshRules');
        } else {
            const error = await response.json();
            showAlert('Error: ' + (error.error || 'Unknown error'), 'danger');
        }
    } catch (error) {
        showAlert('Error: ' + error.message, 'danger');
    }
}

async function toggleRule(ruleId, enabled) {
    try {
        const response = await authenticatedFetch(`/api/v1/filters/${ruleId}/toggle`, {
            method: 'PATCH'
        });

        if (response.ok) {
            showAlert(`Rule ${enabled ? 'enabled' : 'disabled'}!`, 'success');
        } else {
            const error = await response.json();
            showAlert('Error: ' + (error.error || 'Unknown error'), 'danger');
        }
    } catch (error) {
        showAlert('Error: ' + error.message, 'danger');
    }
}

// DNS Query Testing
async function testDNSQuery(event) {
    event.preventDefault();

    const domain = document.getElementById('test-domain').value;
    const recordType = document.getElementById('record-type').value;
    const resultDiv = document.getElementById('query-result');

    if (!domain) {
        showAlert('Please enter a domain name', 'warning');
        return;
    }

    resultDiv.innerHTML = '<div class="spinner"></div> Querying...';

    try {
        const response = await authenticatedFetch(`/api/v1/dns/query?domain=${encodeURIComponent(domain)}&type=${encodeURIComponent(recordType)}`);

        if (response.ok) {
            const data = await response.json();
            resultDiv.innerHTML = '<pre>' + formatQueryResult(data) + '</pre>';
        } else {
            const error = await response.json();
            resultDiv.innerHTML = `<span style="color: var(--danger-color)">Error: ${error.error || 'Unknown error'}</span>`;
        }
    } catch (error) {
        resultDiv.innerHTML = `<span style="color: var(--danger-color)">Error: ${error.message}</span>`;
    }
}

function formatQueryResult(data) {
    return JSON.stringify(data, null, 2);
}

// AI Agent
async function analyzeDomain(event) {
    event.preventDefault();

    const domain = document.getElementById('analyze-domain').value;
    const resultDiv = document.getElementById('analysis-result');

    if (!domain) {
        showAlert('Please enter a domain name', 'warning');
        return;
    }

    resultDiv.innerHTML = '<div class="spinner"></div> Analyzing with AI...';

    try {
        const response = await authenticatedFetch('/api/v1/agent/analyze', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({domain: domain})
        });

        if (response.ok) {
            const data = await response.json();
            resultDiv.innerHTML = formatAnalysisResult(data);
        } else {
            const error = await response.json();
            resultDiv.innerHTML = `<span style="color: var(--danger-color)">Error: ${error.error || 'Analysis failed'}</span>`;
        }
    } catch (error) {
        resultDiv.innerHTML = `<span style="color: var(--danger-color)">Error: ${error.message}</span>`;
    }
}

function formatAnalysisResult(data) {
    let html = '<div class="analysis-result">';
    html += `<p><strong>Domain:</strong> ${data.domain || 'N/A'}</p>`;
    html += `<p><strong>Risk Level:</strong> <span class="badge badge-${getRiskBadgeClass(data.riskLevel)}">${data.riskLevel || 'Unknown'}</span></p>`;
    html += `<p><strong>Is Threat:</strong> ${data.isThreat ? 'Yes' : 'No'}</p>`;
    if (data.analysis) {
        html += `<p><strong>Analysis:</strong></p><pre>${data.analysis}</pre>`;
    }
    if (data.recommendations && data.recommendations.length > 0) {
        html += '<p><strong>Recommendations:</strong></p><ul>';
        data.recommendations.forEach(rec => {
            html += `<li>${rec}</li>`;
        });
        html += '</ul>';
    }
    html += '</div>';
    return html;
}

function getRiskBadgeClass(riskLevel) {
    switch (riskLevel?.toLowerCase()) {
        case 'high':
            return 'danger';
        case 'medium':
            return 'warning';
        case 'low':
            return 'success';
        default:
            return 'info';
    }
}

// Cache Management
async function clearCache() {
    if (!confirm('Are you sure you want to clear the cache?')) {
        return;
    }

    try {
        const response = await authenticatedFetch('/api/v1/admin/cache/clear', {
            method: 'POST'
        });

        if (response.ok) {
            showAlert('Cache cleared successfully!', 'success');
            void loadDashboardStats();
        } else {
            const error = await response.json();
            showAlert('Error: ' + (error.error || 'Failed to clear cache'), 'danger');
        }
    } catch (error) {
        if (error.message !== 'Authentication required') {
            showAlert('Error: ' + error.message, 'danger');
        }
    }
}

// ====== ENTERTAINMENT FEATURES ======

/**
 * Initialize all entertainment and interactive features
 */
function initEntertainmentFeatures() {
    // Create floating particles background
    createParticleBackground();

    // Set up easter egg listeners
    initEasterEggs();

    // Initialize fun animations
    initFunAnimations();

    // Add confetti for successful operations
    initConfetti();
}

/**
 * Create animated floating particles in the background
 */
function createParticleBackground() {
    const particleCount = 15;

    for (let i = 0; i < particleCount; i++) {
        const particle = document.createElement('div');
        particle.className = 'particle';

        // Random positioning
        const x = Math.random() * window.innerWidth;
        const y = Math.random() * window.innerHeight;
        const delay = Math.random() * 20;
        const duration = 20 + Math.random() * 10;

        particle.style.left = x + 'px';
        particle.style.top = y + 'px';
        particle.style.animationDelay = delay + 's';
        particle.style.animationDuration = duration + 's';
        particle.style.zIndex = '-1';

        document.body.appendChild(particle);
    }
}

/**
 * Initialize easter eggs and hidden interactions
 */
function initEasterEggs() {
    let clickSequence = [];
    const secretSequence = ['d', 'n', 's'];

    // Easter egg: Type "dns" to trigger special effects
    document.addEventListener('keypress', function (e) {
        const key = e.key.toLowerCase();
        clickSequence.push(key);
        clickSequence = clickSequence.slice(-3);

        if (clickSequence.join('') === secretSequence.join('')) {
            triggerDNSEasterEgg();
            clickSequence = [];
        }
    });

    // Easter egg: Double-click the logo
    const logo = document.querySelector('.logo h1');
    if (logo) {
        logo.style.cursor = 'pointer';
        logo.addEventListener('dblclick', triggerLogoAnimation);
    }

    // Easter egg: Click shield stats 5 times for achievement
    let shieldClicks = 0;
    const statCards = document.querySelectorAll('.stat-card');
    statCards.forEach(card => {
        card.style.cursor = 'pointer';
        card.addEventListener('click', function () {
            shieldClicks++;
            if (shieldClicks === 5) {
                showAchievement('🎯 Click Master', 'You clicked 5 stat cards!');
                shieldClicks = 0;
                this.classList.add('wiggle');
                setTimeout(() => this.classList.remove('wiggle'), 300);
            }
        });
    });
}

/**
 * DNS Easter Egg - Trigger special animation
 */
function triggerDNSEasterEgg() {
    const header = document.querySelector('.header');
    if (header) {
        header.style.background = 'linear-gradient(45deg, #00ff88, #00d9ff, #9d00ff, #00ffff, #00ff88)';
        header.style.backgroundSize = '200% 200%';
        header.style.animation = 'slideIn 0.5s ease-out, gradientShift 2s linear infinite';

        showAchievement('🛡️ DNS Master', 'You discovered the DNS Easter Egg!');

        // Play notification sound if available
        playNotificationSound();

        // Reset after animation
        setTimeout(() => {
            header.style.background = 'rgba(10, 15, 26, 0.8)';
            header.style.animation = '';
        }, 3000);
    }
}

/**
 * Logo animation easter egg
 */
function triggerLogoAnimation() {
    const logo = document.querySelector('.logo h1');
    if (logo) {
        logo.classList.add('rotating-icon');
        showAchievement('🎪 Logo Master', 'Wow, you found the spinning logo!');
        playNotificationSound();

        setTimeout(() => logo.classList.remove('rotating-icon'), 2000);
    }
}

/**
 * Show achievement badge with animation
 */
function showAchievement(title, description) {
    const container = document.getElementById('alerts-container') || document.querySelector('main .container');

    if (!container) {
        return;
    }

    const achievement = document.createElement('div');
    achievement.className = 'achievement-badge';
    achievement.innerHTML = `<strong>${title}</strong> - ${description}`;
    achievement.style.position = 'fixed';
    achievement.style.top = '100px';
    achievement.style.right = '20px';
    achievement.style.zIndex = '9999';
    achievement.style.minWidth = '300px';

    container.insertBefore(achievement, container.firstChild);

    // Auto-remove after 3 seconds
    setTimeout(() => {
        achievement.style.animation = 'fadeIn 0.3s ease-out reverse';
        setTimeout(() => achievement.remove(), 300);
    }, 3000);
}

/**
 * Initialize fun animations on page load
 */
function initFunAnimations() {
    // Animate all cards on load
    const cards = document.querySelectorAll('.card');
    cards.forEach((card, index) => {
        card.style.animationDelay = (index * 0.1) + 's';
    });

    // Add fun hover tooltips
    document.querySelectorAll('.btn-primary').forEach(btn => {
        btn.addEventListener('mouseenter', function () {
            const messages = [
                '✨ Let\'s go!',
                '🚀 Ready?',
                '⚡ Click me!',
                '💫 Do it!',
                '🎯 Let\'s go!'
            ];
            this.title = messages[Math.floor(Math.random() * messages.length)];
        });
    });
}

/**
 * Initialize confetti on success
 */
function initConfetti() {
    // Override showAlert to add confetti on success
    const originalShowAlert = window.showAlert;

    window.showAlert = function (message, type) {
        originalShowAlert(message, type);

        if (type === 'success') {
            triggerConfetti();
        }
    };
}

/**
 * Trigger confetti animation
 */
function triggerConfetti() {
    const confettiPieces = 30;
    const colors = ['#00ff88', '#00d9ff', '#00ffff', '#9d00ff', '#00cc66'];

    for (let i = 0; i < confettiPieces; i++) {
        const confetti = document.createElement('div');
        confetti.style.position = 'fixed';
        confetti.style.width = '10px';
        confetti.style.height = '10px';
        const color = colors[Math.floor(Math.random() * colors.length)];
        confetti.style.backgroundColor = color;
        confetti.style.borderRadius = '50%';
        confetti.style.left = Math.random() * window.innerWidth + 'px';
        confetti.style.top = '-10px';
        confetti.style.pointerEvents = 'none';
        confetti.style.zIndex = '9998';
        confetti.style.boxShadow = `0 0 8px ${color}`;

        document.body.appendChild(confetti);

        const startX = Math.random() * window.innerWidth;
        const endX = startX + (Math.random() - 0.5) * 300;
        const duration = 2 + Math.random();

        confetti.animate([
            {
                transform: 'translateY(0) translateX(0) rotate(0deg)',
                opacity: 1
            },
            {
                transform: `translateY(${window.innerHeight}px) translateX(${endX - startX}px) rotate(360deg)`,
                opacity: 0
            }
        ], {
            duration: duration * 1000,
            easing: 'cubic-bezier(0.25, 0.46, 0.45, 0.94)'
        });

        setTimeout(() => confetti.remove(), duration * 1000);
    }
}

/**
 * Play notification sound (silent fallback)
 */
function playNotificationSound() {
    try {
        // Create a simple beep sound using Web Audio API
        const AudioContextClass = window.AudioContext || window.webkitAudioContext;
        const audioContext = new AudioContextClass();
        const oscillator = audioContext.createOscillator();
        const gainNode = audioContext.createGain();

        oscillator.connect(gainNode);
        gainNode.connect(audioContext.destination);

        oscillator.frequency.value = 800;
        oscillator.type = 'sine';

        gainNode.gain.setValueAtTime(0.3, audioContext.currentTime);
        gainNode.gain.exponentialRampToValueAtTime(0.01, audioContext.currentTime + 0.1);

        oscillator.start(audioContext.currentTime);
        oscillator.stop(audioContext.currentTime + 0.1);
    } catch (e) {
        // Silent fail - some browsers don't support Web Audio API
    }
}

/**
 * Enhanced alert display with animations
 */
function showAlert(message, type = 'info') {
    const container = document.getElementById('alerts-container');
    if (!container) {
        return;
    }

    const alert = document.createElement('div');
    alert.className = `alert alert-${type} scale-up`;

    const icons = {
        success: '✅',
        danger: '❌',
        warning: '⚠️',
        info: 'ℹ️'
    };

    alert.innerHTML = `${icons[type] || type} ${message}`;
    container.insertBefore(alert, container.firstChild);

    // Auto-remove after 4 seconds
    setTimeout(() => {
        alert.style.animation = 'fadeIn 0.3s ease-out reverse';
        setTimeout(() => alert.remove(), 300);
    }, 4000);
}

/**
 * Format numbers with commas and thousand separators
 */
function formatNumber(num) {
    return Math.round(num).toLocaleString('en-US');
}

// Category Filter
function filterByCategory(event, category) {
    const buttons = document.querySelectorAll('.filter-btn');
    buttons.forEach(btn => btn.classList.remove('active'));
    if (event && event.target) {
        event.target.classList.add('active');
    }

    const url = category ? `/ui/filters/list?category=${category}` : '/ui/filters/list';
    htmx.ajax('GET', url, '#rules-table-body');
}

// Handle closing modal on outside click
document.addEventListener('click', function (event) {
    if (event.target.classList.contains('modal')) {
        event.target.classList.remove('active');
    }
});

// Handle escape key to close modal
document.addEventListener('keydown', function (event) {
    if (event.key === 'Escape') {
        document.querySelectorAll('.modal.active').forEach(modal => {
            modal.classList.remove('active');
        });
    }
});

