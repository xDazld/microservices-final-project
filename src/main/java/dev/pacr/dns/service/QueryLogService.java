package dev.pacr.dns.service;

import dev.pacr.dns.storage.DNSLogRepository;
import dev.pacr.dns.storage.model.DNSLogEntry;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.time.Instant;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service for storing and retrieving DNS query logs
 * <p>
 * Retrieves logs from the database (MongoDB) instead of maintaining an in-memory buffer.
 */
@ApplicationScoped
public class QueryLogService {
    
    private static final Logger LOG = Logger.getLogger(QueryLogService.class);
    private static final int DEFAULT_LIMIT = 100;
    
    @Inject
    DNSLogRepository logRepository;
    
    /**
     * Log a DNS query (compat placeholder retained for API stability)
     * <p>
     * Note: Actual persistence is handled in DNSLoggingService.logQuery(). This method is kept to
     * avoid breaking existing call sites; it will only log a debug message.
     */
    public void logQuery(String domain, String queryType, String status, int rcode,
                         List<String> answers, String sourceIp) {
        LOG.debugf("QueryLogService.logQuery called (noop) for domain=%s, type=%s, status=%s",
                domain, queryType, status);
        // Persistence is performed by DNSLoggingService via DNSLogRepository
    }
    
    /**
     * Get recent query logs
     *
     * @param limit Maximum number of entries to return
     * @return List of recent query log entries
     */
    public List<Map<String, Object>> getRecentQueries(int limit) {
        int effectiveLimit = limit > 0 ? limit : DEFAULT_LIMIT;
        try {
            List<DNSLogEntry> entries = logRepository.findAll().page(0, effectiveLimit).list();
            // In-memory sort by timestamp desc
            entries.sort(Comparator.comparing(
                            (DNSLogEntry e) -> e.timestamp != null ? e.timestamp : Instant.EPOCH)
                    .reversed());
            return entries.stream().map(this::toMap).collect(Collectors.toList());
        } catch (RuntimeException e) {
            LOG.errorf(e, "Failed to retrieve recent queries");
            return List.of();
        }
    }
    
    /**
     * Get all query logs
     *
     * @return List of all query log entries
     */
    public List<Map<String, Object>> getAllQueries() {
        try {
            List<DNSLogEntry> entries = logRepository.findAll().list();
            entries.sort(Comparator.comparing(
                            (DNSLogEntry e) -> e.timestamp != null ? e.timestamp : Instant.EPOCH)
                    .reversed());
            return entries.stream().map(this::toMap).collect(Collectors.toList());
        } catch (RuntimeException e) {
            LOG.errorf(e, "Failed to retrieve all queries");
            return List.of();
        }
    }
    
    /**
     * Get query logs filtered by status
     *
     * @param status The status to filter by (ALLOWED, BLOCKED, THREAT, etc.)
     * @param limit  Maximum number of entries to return
     * @return List of query log entries with the specified status
     */
    public List<Map<String, Object>> getQueriesByStatus(String status, int limit) {
        if (status == null || status.isBlank()) {
            return getRecentQueries(limit);
        }
        int effectiveLimit = limit > 0 ? limit : DEFAULT_LIMIT;
        try {
            List<DNSLogEntry> entries =
                    logRepository.find("status", status).page(0, effectiveLimit).list();
            entries.sort(Comparator.comparing(
                            (DNSLogEntry e) -> e.timestamp != null ? e.timestamp : Instant.EPOCH)
                    .reversed());
            return entries.stream().map(this::toMap).collect(Collectors.toList());
        } catch (RuntimeException e) {
            LOG.errorf(e, "Failed to retrieve queries by status: %s", status);
            return List.of();
        }
    }
    
    /**
     * Get query logs filtered by domain
     *
     * @param domain The domain to search for
     * @param limit  Maximum number of entries to return
     * @return List of query log entries for the specified domain
     */
    public List<Map<String, Object>> getQueriesByDomain(String domain, int limit) {
        if (domain == null || domain.isBlank()) {
            return getRecentQueries(limit);
        }
        int effectiveLimit = limit > 0 ? limit : DEFAULT_LIMIT;
        try {
            List<DNSLogEntry> entries =
                    logRepository.find("{ 'domain': { $regex: ?1, $options: 'i' } }", domain)
                            .page(0, effectiveLimit).list();
            entries.sort(Comparator.comparing(
                            (DNSLogEntry e) -> e.timestamp != null ? e.timestamp : Instant.EPOCH)
                    .reversed());
            return entries.stream().map(this::toMap).collect(Collectors.toList());
        } catch (RuntimeException e) {
            LOG.errorf(e, "Failed to retrieve queries by domain: %s", domain);
            return List.of();
        }
    }
    
    /**
     * Get query statistics
     *
     * @return Map containing query statistics
     */
    public Map<String, Object> getQueryStats() {
        Map<String, Object> stats = new LinkedHashMap<>();
        try {
            long totalQueries = logRepository.count();
            long blockedQueries = logRepository.count("status", "BLOCK");
            long allowedQueries = logRepository.count("status", "ALLOW");
            long threatQueries = logRepository.count("status", "THREAT");
            
            stats.put("totalQueries", totalQueries);
            stats.put("blockedQueries", blockedQueries);
            stats.put("allowedQueries", allowedQueries);
            stats.put("threatQueries", threatQueries);
            
            if (totalQueries > 0) {
                stats.put("blockRate",
                        String.format("%.1f%%", (blockedQueries * 100.0) / totalQueries));
                stats.put("threatRate",
                        String.format("%.1f%%", (threatQueries * 100.0) / totalQueries));
            } else {
                stats.put("blockRate", "0%");
                stats.put("threatRate", "0%");
            }
        } catch (Exception e) {
            LOG.errorf(e, "Failed to compute query stats");
            stats.put("totalQueries", 0L);
            stats.put("blockedQueries", 0L);
            stats.put("allowedQueries", 0L);
            stats.put("threatQueries", 0L);
            stats.put("blockRate", "0%");
            stats.put("threatRate", "0%");
        }
        return stats;
    }
    
    /**
     * Clear all query logs (from database)
     */
    public void clearLogs() {
        try {
            logRepository.deleteAll();
            LOG.info("Query logs cleared (database)");
        } catch (Exception e) {
            LOG.errorf(e, "Failed to clear query logs from database");
        }
    }
    
    private Map<String, Object> toMap(DNSLogEntry entry) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("domain", entry.domain);
        // Query type not currently persisted in DNSLogEntry; set to "A" placeholder if unknown
        map.put("queryType", "A");
        map.put("status", entry.status);
        map.put("rcode", entry.rcode);
        map.put("answers", entry.answers != null ? entry.answers : List.of());
        map.put("sourceIp", "-");
        map.put("timestamp",
                (entry.timestamp != null ? entry.timestamp : Instant.EPOCH).toString());
        return map;
    }
}
