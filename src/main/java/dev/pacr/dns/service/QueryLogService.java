package dev.pacr.dns.service;

import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service for storing and retrieving DNS query logs
 * <p>
 * Maintains an in-memory circular buffer of recent DNS queries for display in the UI. Older queries
 * are automatically purged when the buffer reaches maximum size.
 */
@ApplicationScoped
public class QueryLogService {
	
	private static final Logger LOG = Logger.getLogger(QueryLogService.class);
	private static final int MAX_LOG_ENTRIES = 1000; // Keep last 1000 queries
	
	private final LinkedList<QueryLogEntry> queryLog = new LinkedList<>();
	private final Object lock = new Object();
	
	/**
	 * Log a DNS query
	 *
	 * @param domain    The domain queried
	 * @param queryType The DNS record type (A, AAAA, MX, etc.)
	 * @param status    The status (ALLOWED, BLOCKED, THREAT, etc.)
	 * @param rcode     The DNS response code
	 * @param answers   The answers returned (if any)
	 * @param sourceIp  The source IP of the query
	 */
	public void logQuery(String domain, String queryType, String status, int rcode,
						 List<String> answers, String sourceIp) {
		synchronized (lock) {
			QueryLogEntry entry =
					new QueryLogEntry(domain, queryType, status, rcode, answers, sourceIp);
			queryLog.addFirst(entry);
			
			// Remove oldest entries if we exceed max size
			while (queryLog.size() > MAX_LOG_ENTRIES) {
				queryLog.removeLast();
			}
			
			LOG.debugf("Logged DNS query: %s (%s) - %s", domain, queryType, status);
		}
	}
	
	/**
	 * Get recent query logs
	 *
	 * @param limit Maximum number of entries to return
	 * @return List of recent query log entries
	 */
	public List<Map<String, Object>> getRecentQueries(int limit) {
		synchronized (lock) {
			return queryLog.stream().limit(Math.min(limit, queryLog.size()))
					.map(QueryLogEntry::toMap).collect(Collectors.toList());
		}
	}
	
	/**
	 * Get all query logs
	 *
	 * @return List of all query log entries
	 */
	public List<Map<String, Object>> getAllQueries() {
		synchronized (lock) {
			return queryLog.stream().map(QueryLogEntry::toMap).collect(Collectors.toList());
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
		synchronized (lock) {
			return queryLog.stream().filter(entry -> entry.status.equalsIgnoreCase(status))
					.limit(Math.min(limit, queryLog.size())).map(QueryLogEntry::toMap)
					.collect(Collectors.toList());
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
		synchronized (lock) {
			return queryLog.stream().filter(entry -> entry.domain.contains(domain))
					.limit(Math.min(limit, queryLog.size())).map(QueryLogEntry::toMap)
					.collect(Collectors.toList());
		}
	}
	
	/**
	 * Get query statistics
	 *
	 * @return Map containing query statistics
	 */
	public Map<String, Object> getQueryStats() {
		synchronized (lock) {
			Map<String, Object> stats = new LinkedHashMap<>();
			
			long totalQueries = queryLog.size();
			long blockedQueries =
					queryLog.stream().filter(q -> q.status.equalsIgnoreCase("BLOCKED")).count();
			long allowedQueries =
					queryLog.stream().filter(q -> q.status.equalsIgnoreCase("ALLOWED")).count();
			long threatQueries =
					queryLog.stream().filter(q -> q.status.equalsIgnoreCase("THREAT")).count();
			
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
			
			return stats;
		}
	}
	
	/**
	 * Clear all query logs
	 */
	public void clearLogs() {
		synchronized (lock) {
			queryLog.clear();
			LOG.info("Query logs cleared");
		}
	}
	
	/**
	 * Inner class for query log entries
	 */
	private static class QueryLogEntry {
		public final String domain;
		public final String queryType;
		public final String status;
		public final int rcode;
		public final List<String> answers;
		public final String sourceIp;
		public final Instant timestamp;
		
		public QueryLogEntry(String domain, String queryType, String status, int rcode,
							 List<String> answers, String sourceIp) {
			this.domain = domain;
			this.queryType = queryType;
			this.status = status;
			this.rcode = rcode;
			this.answers = answers != null ? answers : new ArrayList<>();
			this.sourceIp = sourceIp;
			this.timestamp = Instant.now();
		}
		
		public Map<String, Object> toMap() {
			Map<String, Object> map = new LinkedHashMap<>();
			map.put("domain", domain);
			map.put("queryType", queryType);
			map.put("status", status);
			map.put("rcode", rcode);
			map.put("answers", answers);
			map.put("sourceIp", sourceIp);
			map.put("timestamp", timestamp.toString());
			return map;
		}
	}
}
