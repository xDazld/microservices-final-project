package dev.pacr.dns.service;

import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Service for recording and tracking endpoint usage statistics
 * <p>
 * This service maintains per-endpoint metrics including: - Total number of
 * requests - Total number
 * of successful responses - Total number of failed responses - Average response
 * time - Min/max
 * response times - HTTP status code distribution - Total bytes sent/received
 * <p>
 * Thread-safe for concurrent access.
 */
@ApplicationScoped
public class EndpointStatisticsService {

	private static final Logger LOG = Logger.getLogger(EndpointStatisticsService.class);
	// Endpoint statistics keyed by "METHOD ENDPOINT"
	private final Map<String, EndpointStatistics> statistics = new ConcurrentHashMap<>();
	private final ReadWriteLock globalLock = new ReentrantReadWriteLock();

	/**
	 * Record a request to an endpoint
	 *
	 * @param method         HTTP method (GET, POST, etc.)
	 * @param endpoint       API endpoint path
	 * @param responseTimeMs Response time in milliseconds
	 * @param statusCode     HTTP status code
	 * @param bytesIn        Bytes received in request
	 * @param bytesOut       Bytes sent in response
	 */
	public void recordRequest(String method, String endpoint, long responseTimeMs, int statusCode,
			long bytesIn, long bytesOut) {
		String key = method + ' ' + endpoint;

		statistics.computeIfAbsent(key, k -> new EndpointStatistics(endpoint, method))
				.recordRequest(responseTimeMs, statusCode, bytesIn, bytesOut);

		LOG.debugf("Recorded request: %s - %d ms - Status: %d", key, responseTimeMs, statusCode);
	}

	/**
	 * Get statistics for a specific endpoint
	 *
	 * @param method   HTTP method
	 * @param endpoint API endpoint path
	 * @return Statistics map or null if endpoint not accessed
	 */
	public Map<String, Object> getEndpointStatistics(String method, String endpoint) {
		String key = method + ' ' + endpoint;
		EndpointStatistics stats = statistics.get(key);
		return stats != null ? stats.toMap() : null;
	}

	/**
	 * Get all endpoint statistics
	 *
	 * @return Map of all endpoints and their statistics
	 */
	public Map<String, Object> getAllStatistics() {
		globalLock.readLock().lock();
		try {
			
			// Overall summary
			long totalRequests = 0;
			long totalSuccessful = 0;
			long totalFailed = 0;
			double totalAvgResponseTime = 0;
			
			Collection<Map<String, Object>> endpoints = new ArrayList<>();

			for (EndpointStatistics stat : statistics.values()) {
				endpoints.add(stat.toMap());
				totalRequests += stat.getTotalRequests();
				totalSuccessful += stat.getSuccessfulRequests();
				totalFailed += stat.getFailedRequests();
				totalAvgResponseTime += stat.getAverageResponseTime();
			}

			Map<String, Object> summary = new LinkedHashMap<>();
			summary.put("totalEndpointsAccessed", statistics.size());
			summary.put("totalRequests", totalRequests);
			summary.put("totalSuccessful", totalSuccessful);
			summary.put("totalFailed", totalFailed);
			summary.put("successRate",
					totalRequests > 0 ? String.format("%.2f%%", (totalSuccessful * 100.0) / totalRequests) : "N/A");
			summary.put("averageResponseTimeAcrossAllEndpoints", !statistics.isEmpty() ?
					String.format("%.2f ms", totalAvgResponseTime / statistics.size()) : "N/A");
			
			Map<String, Object> allStats = new LinkedHashMap<>();
			allStats.put("summary", summary);
			allStats.put("endpoints", endpoints.isEmpty() ? null : endpoints);

			return allStats;
		} finally {
			globalLock.readLock().unlock();
		}
	}

	/**
	 * Get statistics for a specific path pattern (useful for grouped stats)
	 *
	 * @param pathPattern Pattern to match endpoints (e.g., "/api/v1/dns*")
	 * @return List of matching endpoint statistics
	 */
	public List<Map<String, Object>> getStatisticsByPattern(String pathPattern) {
		List<Map<String, Object>> result = new ArrayList<>();

		for (EndpointStatistics stat : statistics.values()) {
			if (stat.endpoint.startsWith(pathPattern.replace("*", ""))) {
				result.add(stat.toMap());
			}
		}

		return result;
	}

	/**
	 * Reset all statistics (for testing or maintenance)
	 */
	public void resetStatistics() {
		globalLock.writeLock().lock();
		try {
			statistics.clear();
			LOG.info("All endpoint statistics have been reset");
		} finally {
			globalLock.writeLock().unlock();
		}
	}

	/**
	 * Reset statistics for a specific endpoint
	 *
	 * @param method   HTTP method
	 * @param endpoint API endpoint path
	 */
	public void resetEndpointStatistics(String method, String endpoint) {
		globalLock.writeLock().lock();
		try {
			String key = method + ' ' + endpoint;
			statistics.remove(key);
			LOG.infof("Statistics reset for: %s", key);
		} finally {
			globalLock.writeLock().unlock();
		}
	}

	/**
	 * Get count of unique endpoints tracked
	 */
	public int getEndpointCount() {
		return statistics.size();
	}

	/**
	 * Statistics for a single endpoint
	 */
	public static class EndpointStatistics {
		private final String endpoint;
		private final String method;
		private final Map<Integer, Long> statusCodeCounts = new ConcurrentHashMap<>();
		private final ReadWriteLock lock = new ReentrantReadWriteLock();
		private long totalRequests = 0;
		private long successfulRequests = 0;
		private long failedRequests = 0;
		private long totalResponseTime = 0; // milliseconds
		private long minResponseTime = Long.MAX_VALUE;
		private long maxResponseTime = 0;
		private long totalBytesIn = 0;
		private long totalBytesOut = 0;
		private Instant lastAccessTime;
		private final Instant createdTime;

		public EndpointStatistics(String endpoint, String method) {
			this.endpoint = endpoint;
			this.method = method;
			this.createdTime = Instant.now();
			this.lastAccessTime = Instant.now();
		}

		public void recordRequest(long responseTimeMs, int statusCode, long bytesIn,
				long bytesOut) {
			lock.writeLock().lock();
			try {
				totalRequests++;
				totalResponseTime += responseTimeMs;
				minResponseTime = Math.min(minResponseTime, responseTimeMs);
				maxResponseTime = Math.max(maxResponseTime, responseTimeMs);
				totalBytesIn += bytesIn;
				totalBytesOut += bytesOut;
				lastAccessTime = Instant.now();

				// Track status code distribution
				statusCodeCounts.merge(statusCode, 1L, Long::sum);

				// Update success/failure counters
				if (statusCode >= 200 && statusCode < 300) {
					successfulRequests++;
				} else if (statusCode >= 400) {
					failedRequests++;
				}
			} finally {
				lock.writeLock().unlock();
			}
		}

		public Map<String, Object> toMap() {
			lock.readLock().lock();
			try {
				Map<String, Object> stats = new LinkedHashMap<>();
				stats.put("endpoint", endpoint);
				stats.put("method", method);
				stats.put("totalRequests", totalRequests);
				stats.put("successfulRequests", successfulRequests);
				stats.put("failedRequests", failedRequests);
				stats.put("successRate",
						totalRequests > 0 ? String.format("%.2f%%", (successfulRequests * 100.0) / totalRequests)
								: "N/A");
				stats.put("averageResponseTime",
						totalRequests > 0 ? String.format("%.2f ms", totalResponseTime / (double) totalRequests)
								: "N/A");
				stats.put("minResponseTime",
						minResponseTime == Long.MAX_VALUE ? "N/A" : minResponseTime + " ms");
				stats.put("maxResponseTime", maxResponseTime + " ms");
				stats.put("totalBytesIn", totalBytesIn);
				stats.put("totalBytesOut", totalBytesOut);
				stats.put("statusCodeDistribution", new LinkedHashMap<>(statusCodeCounts));
				stats.put("createdTime", createdTime.toString());
				stats.put("lastAccessTime", lastAccessTime.toString());
				return stats;
			} finally {
				lock.readLock().unlock();
			}
		}

		// Getters for internal use
		public long getTotalRequests() {
			lock.readLock().lock();
			try {
				return totalRequests;
			} finally {
				lock.readLock().unlock();
			}
		}

		public double getAverageResponseTime() {
			lock.readLock().lock();
			try {
				return totalRequests > 0 ? totalResponseTime / (double) totalRequests : 0;
			} finally {
				lock.readLock().unlock();
			}
		}

		public long getSuccessfulRequests() {
			lock.readLock().lock();
			try {
				return successfulRequests;
			} finally {
				lock.readLock().unlock();
			}
		}

		public long getFailedRequests() {
			lock.readLock().lock();
			try {
				return failedRequests;
			} finally {
				lock.readLock().unlock();
			}
		}
	}
}
