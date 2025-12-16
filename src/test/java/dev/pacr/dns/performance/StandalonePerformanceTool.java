package dev.pacr.dns.performance;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.random.RandomGenerator;
import java.util.stream.Collectors;

/**
 * Standalone Performance Testing Tool for DNS Shield
 * <p>
 * This tool can be run independently from the command line to test the DNS Shield microservice
 * under load.
 * <p>
 * Usage: java -cp target/classes:target/test-classes
 * dev.pacr.dns.performance.StandalonePerformanceTool [base-url]
 * <p>
 * Example: java -cp target/classes:target/test-classes
 * dev.pacr.dns.performance.StandalonePerformanceTool <a href="http://localhost:8080">...</a>
 *
 * @author Patrick Rafferty
 */
public class StandalonePerformanceTool {
	
	private static final int WARMUP_REQUESTS = 50;
	private static final int[] CONCURRENT_USERS = {1, 5, 10, 25, 50, 100};
	private static final int REQUESTS_PER_USER = 100;
	private static final Duration TIMEOUT = Duration.ofSeconds(30);
	// HttpClient connection pool configuration for performance testing
	private static final int MAX_CONNECTIONS = 200;
	private static String BASE_URL = "http://localhost:8080";
	private static HttpClient httpClient;
	
	public static void main(String[] args) throws Exception {
		if (args.length > 0) {
			BASE_URL = args[0];
		}
		
		System.out.println("=".repeat(80));
		System.out.println("DNS Shield - Standalone Performance Test Tool");
		System.out.println("Timestamp: " + new Date());
		System.out.println("Base URL: " + BASE_URL);
		System.out.println("=".repeat(80));
		
		httpClient =
				HttpClient.newBuilder().connectTimeout(TIMEOUT).version(HttpClient.Version.HTTP_1_1)
						.build();
		
		// Check if service is available
		if (!isServiceAvailable()) {
			System.err.println("\nERROR: Service is not available at " + BASE_URL);
			System.err.println("Please start the DNS Shield service before running this test.");
			System.exit(1);
		}
		
		System.out.println("✓ Service is available and responding");
		
		// Warmup
		System.out.println("\n[WARMUP] Running " + WARMUP_REQUESTS + " warmup requests...");
		warmup();
		System.out.println("✓ Warmup completed");
		
		// Test DNS JSON API endpoint
		System.out.println('\n' + "=".repeat(80));
		System.out.println("Test 1: DNS JSON Query API (/api/v1/dns/query)");
		System.out.println("=".repeat(80));
		Collection<TestResult> allResults = new ArrayList<>(testDNSJsonEndpoint());
		
		// Test DNS-over-HTTPS endpoint (GET)
		System.out.println('\n' + "=".repeat(80));
		System.out.println("Test 2: DNS-over-HTTPS GET (/dns-query)");
		System.out.println("=".repeat(80));
		allResults.addAll(testDNSQueryGetEndpoint());
		
		// Generate summary report
		generateReport(allResults);
		
		System.out.println("\n✓ Performance testing completed successfully!");
	}
	
	private static boolean isServiceAvailable() {
		try {
			String url = BASE_URL + "/api/v1/admin/health";
			HttpRequest request =
					HttpRequest.newBuilder().uri(URI.create(url)).timeout(Duration.ofSeconds(5))
							.GET().build();
			
			HttpResponse<String> response =
					httpClient.send(request, HttpResponse.BodyHandlers.ofString());
			
			return response.statusCode() == 200;
		} catch (Exception e) {
			return false;
		}
	}
	
	private static void warmup() {
		String url = BASE_URL + "/api/v1/dns/query?domain=google.com&type=A";
		int successful = 0;
		
		for (int i = 0; i < WARMUP_REQUESTS; i++) {
			try {
				HttpRequest request =
						HttpRequest.newBuilder().uri(URI.create(url)).timeout(TIMEOUT).GET()
								.build();
				HttpResponse<String> response =
						httpClient.send(request, HttpResponse.BodyHandlers.ofString());
				if (response.statusCode() == 200) {
					successful++;
				}
			} catch (Exception e) {
				// Continue warmup even if some requests fail
			}
			
			if ((i + 1) % 10 == 0) {
				System.out.print('.');
			}
		}
		
		System.out.println(" (" + successful + '/' + WARMUP_REQUESTS + " successful)");
	}
	
	private static List<TestResult> testDNSJsonEndpoint() throws Exception {
		List<TestResult> results = new ArrayList<>();
		String[] testDomains =
				{"google.com", "github.com", "stackoverflow.com", "example.com", "mozilla.org",
						"amazon.com", "ads.doubleclick.net", "www.malware-example.com"};
		
		for (int concurrentUsers : CONCURRENT_USERS) {
			System.out.println("\n--- Testing with " + concurrentUsers + " concurrent users ---");
			System.out.print("Progress: ");
			
			List<Long> allLatencies;
			int successfulRequests;
			int failedRequests;
			double testDurationSeconds;
			try (ExecutorService executor = Executors.newFixedThreadPool(concurrentUsers)) {
				Collection<Future<List<Long>>> futures = new ArrayList<>();
				
				long testStartTime = System.currentTimeMillis();
				
				for (int i = 0; i < concurrentUsers; i++) {
					futures.add(executor.submit(() -> {
						List<Long> latencies = new ArrayList<>();
						RandomGenerator random = new Random();
						
						for (int j = 0; j < REQUESTS_PER_USER; j++) {
							String domain = testDomains[random.nextInt(testDomains.length)];
							String url =
									BASE_URL + "/api/v1/dns/query?domain=" + domain + "&type=A";
							
							long startTime = System.nanoTime();
							try {
								HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url))
										.timeout(TIMEOUT).GET().build();
								
								HttpResponse<String> response = httpClient.send(request,
										HttpResponse.BodyHandlers.ofString());
								
								long endTime = System.nanoTime();
								
								if (response.statusCode() == 200) {
									long latencyMs = (endTime - startTime) / 1_000_000;
									latencies.add(latencyMs);
								}
							} catch (Exception e) {
								// Request failed, don't record latency
							}
						}
						return latencies;
					}));
				}
				
				// Show progress
				int completed = 0;
				int lastPercent = 0;
				while (completed < futures.size()) {
					completed = (int) futures.stream().filter(Future::isDone).count();
					int percent = (completed * 100) / futures.size();
					if (percent > lastPercent && percent % 10 == 0) {
						System.out.print(percent + "% ");
						lastPercent = percent;
					}
					Thread.sleep(100);
				}
				System.out.println("100%");
				
				// Collect results
				allLatencies = new ArrayList<>();
				successfulRequests = 0;
				failedRequests = 0;
				
				for (Future<List<Long>> future : futures) {
					try {
						List<Long> userLatencies = future.get();
						allLatencies.addAll(userLatencies);
						successfulRequests += userLatencies.size();
						failedRequests += (REQUESTS_PER_USER - userLatencies.size());
					} catch (Exception e) {
						failedRequests += REQUESTS_PER_USER;
					}
				}
				
				long testEndTime = System.currentTimeMillis();
				testDurationSeconds = (testEndTime - testStartTime) / 1000.0;
				
				executor.shutdown();
				executor.awaitTermination(1, TimeUnit.MINUTES);
			}
			
			if (!allLatencies.isEmpty()) {
				TestResult result =
						calculateStatistics("DNS JSON API", concurrentUsers, allLatencies,
								successfulRequests, failedRequests, testDurationSeconds);
				results.add(result);
				printResult(result);
			}
		}
		
		return results;
	}
	
	private static List<TestResult> testDNSQueryGetEndpoint() throws Exception {
		List<TestResult> results = new ArrayList<>();
		
		// Base64url encoded DNS queries
		String[] dnsQueries = {"AAABAAABAAAAAAAAA3d3dwdleGFtcGxlA2NvbQAAAQAB",
				"AAABAAABAAAAAAAABmdvb2dsZQNjb20AAAEAAQ",
				"AAABAAABAAAAAAAABmdpdGh1YgNjb20AAAEAAQ"};
		
		for (int concurrentUsers : CONCURRENT_USERS) {
			System.out.println("\n--- Testing with " + concurrentUsers + " concurrent users ---");
			System.out.print("Progress: ");
			
			List<Long> allLatencies;
			int successfulRequests;
			int failedRequests;
			double testDurationSeconds;
			try (ExecutorService executor = Executors.newFixedThreadPool(concurrentUsers)) {
				Collection<Future<List<Long>>> futures = new ArrayList<>();
				
				long testStartTime = System.currentTimeMillis();
				
				for (int i = 0; i < concurrentUsers; i++) {
					futures.add(executor.submit(() -> {
						List<Long> latencies = new ArrayList<>();
						RandomGenerator random = new Random();
						
						for (int j = 0; j < REQUESTS_PER_USER; j++) {
							String dns = dnsQueries[random.nextInt(dnsQueries.length)];
							String url = BASE_URL + "/dns-query?dns=" + dns;
							
							long startTime = System.nanoTime();
							try {
								HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url))
										.timeout(TIMEOUT)
										.header("Accept", "application/dns-message").GET().build();
								
								HttpResponse<byte[]> response = httpClient.send(request,
										HttpResponse.BodyHandlers.ofByteArray());
								
								long endTime = System.nanoTime();
								
								if (response.statusCode() == 200) {
									long latencyMs = (endTime - startTime) / 1_000_000;
									latencies.add(latencyMs);
								}
							} catch (Exception e) {
								// Request failed
							}
						}
						return latencies;
					}));
				}
				
				// Show progress
				int completed = 0;
				int lastPercent = 0;
				while (completed < futures.size()) {
					completed = (int) futures.stream().filter(Future::isDone).count();
					int percent = (completed * 100) / futures.size();
					if (percent > lastPercent && percent % 10 == 0) {
						System.out.print(percent + "% ");
						lastPercent = percent;
					}
					Thread.sleep(100);
				}
				System.out.println("100%");
				
				// Collect results
				allLatencies = new ArrayList<>();
				successfulRequests = 0;
				failedRequests = 0;
				
				for (Future<List<Long>> future : futures) {
					try {
						List<Long> userLatencies = future.get();
						allLatencies.addAll(userLatencies);
						successfulRequests += userLatencies.size();
						failedRequests += (REQUESTS_PER_USER - userLatencies.size());
					} catch (Exception e) {
						failedRequests += REQUESTS_PER_USER;
					}
				}
				
				long testEndTime = System.currentTimeMillis();
				testDurationSeconds = (testEndTime - testStartTime) / 1000.0;
				
				executor.shutdown();
				executor.awaitTermination(1, TimeUnit.MINUTES);
			}
			
			if (!allLatencies.isEmpty()) {
				TestResult result =
						calculateStatistics("DNS-over-HTTPS GET", concurrentUsers, allLatencies,
								successfulRequests, failedRequests, testDurationSeconds);
				results.add(result);
				printResult(result);
			}
		}
		
		return results;
	}
	
	private static TestResult calculateStatistics(String endpointName, int concurrentUsers,
												  List<Long> latencies, int successfulRequests,
												  int failedRequests, double durationSeconds) {
		Collections.sort(latencies);
		
		double mean = latencies.stream().mapToLong(Long::longValue).average().orElse(0);
		long min = latencies.getFirst();
		long max = latencies.getLast();
		long p50 = latencies.get((int) (latencies.size() * 0.50));
		long p90 = latencies.get((int) (latencies.size() * 0.90));
		long p95 = latencies.get((int) (latencies.size() * 0.95));
		long p99 = latencies.get((int) (latencies.size() * 0.99));
		
		double throughput = successfulRequests / durationSeconds;
		double successRate =
				(double) successfulRequests / (successfulRequests + failedRequests) * 100;
		
		return new TestResult(endpointName, concurrentUsers, successfulRequests, failedRequests,
				durationSeconds, mean, min, max, p50, p90, p95, p99, throughput, successRate);
	}
	
	private static void printResult(TestResult result) {
		System.out.println("\nResults:");
		System.out.printf("  Concurrent Users: %d\n", result.concurrentUsers);
		System.out.printf("  Total Requests: %d\n",
				result.successfulRequests + result.failedRequests);
		System.out.printf("  Successful: %d (%.2f%%)\n", result.successfulRequests,
				result.successRate);
		System.out.printf("  Failed: %d\n", result.failedRequests);
		System.out.printf("  Test Duration: %.2f seconds\n", result.durationSeconds);
		System.out.printf("  Throughput: %.2f requests/sec\n", result.throughput);
		System.out.println("\n  Latency Statistics (milliseconds):");
		System.out.printf("    Min:  %d ms\n", result.min);
		System.out.printf("    Mean: %.2f ms\n", result.mean);
		System.out.printf("    Max:  %d ms\n", result.max);
		System.out.printf("    P50:  %d ms (median)\n", result.p50);
		System.out.printf("    P90:  %d ms\n", result.p90);
		System.out.printf("    P95:  %d ms\n", result.p95);
		System.out.printf("    P99:  %d ms\n", result.p99);
	}
	
	private static void generateReport(Collection<TestResult> allResults) throws IOException {
		System.out.println('\n' + "=".repeat(80));
		System.out.println("PERFORMANCE TEST SUMMARY");
		System.out.println("=".repeat(80));
		
		// Group by endpoint
		Map<String, List<TestResult>> resultsByEndpoint =
				allResults.stream().collect(Collectors.groupingBy(r -> r.endpointName));
		
		for (Map.Entry<String, List<TestResult>> entry : resultsByEndpoint.entrySet()) {
			System.out.println('\n' + entry.getKey());
			System.out.println("-".repeat(80));
			System.out.printf("%-8s %-13s %-10s %-7s %-7s %-7s %-7s %-10s\n", "Users",
					"Throughput",
					"Mean(ms)", "P50", "P90", "P95", "P99", "Success%");
			System.out.println("-".repeat(80));
			
			for (TestResult result : entry.getValue()) {
				System.out.printf("%-8d %-13.2f %-10.2f %-7d %-7d %-7d %-7d %-10.2f\n",
						result.concurrentUsers, result.throughput, result.mean, result.p50,
						result.p90, result.p95, result.p99, result.successRate);
			}
		}
		
		// Generate CSV report
		String csvFilename = "performance-report-" + System.currentTimeMillis() + ".csv";
		try (FileWriter writer = new FileWriter(csvFilename, StandardCharsets.UTF_8)) {
			writer.write(
					"Endpoint,Concurrent Users,Successful Requests,Failed Requests,Duration (s)," +
							"Throughput (req/s),Success Rate (%),Min (ms),Mean (ms),Max (ms)," +
							"P50 (ms),P90 (ms),P95 (ms),P99 (ms)\n");
			
			for (TestResult result : allResults) {
				writer.write(String.format("%s,%d,%d,%d,%.2f,%.2f,%.2f,%d,%.2f,%d,%d,%d,%d,%d\n",
						result.endpointName, result.concurrentUsers, result.successfulRequests,
						result.failedRequests, result.durationSeconds, result.throughput,
						result.successRate, result.min, result.mean, result.max, result.p50,
						result.p90, result.p95, result.p99));
			}
		}
		
		System.out.println('\n' + "=".repeat(80));
		System.out.println("✓ CSV report generated: " + csvFilename);
		System.out.println("=".repeat(80));
		
		// Generate charts using Python
		generateChartsWithPython(csvFilename);
	}
	
	/**
	 * Invokes the Python script to generate entertaining charts from the CSV data
	 */
	private static void generateChartsWithPython(String csvFilename) {
		System.out.println('\n' + "=".repeat(80));
		System.out.println("GENERATING PERFORMANCE CHARTS");
		System.out.println("=".repeat(80));
		
		try {
			// Find the Python script (it's in the same directory as this class)
			String pythonScript = "src/test/java/dev/pacr/dns/performance/generate_charts.py";
			
			// Check if Python is available
			ProcessBuilder checkPython = new ProcessBuilder("python3", "--version");
			Process checkProcess = checkPython.start();
			int checkResult = checkProcess.waitFor();
			
			if (checkResult != 0) {
				System.err.println("⚠️  Python 3 not found. Skipping chart generation.");
				return;
			}
			
			System.out.println("✓ Python 3 found");
			System.out.println("📊 Launching chart generation...");
			System.out.println("   (This may take a few seconds)");
			
			// Run the Python script
			ProcessBuilder pb = new ProcessBuilder("python3", pythonScript, csvFilename);
			pb.inheritIO(); // This allows us to see Python's output in real-time
			Process process = pb.start();
			
			int exitCode = process.waitFor();
			
			if (exitCode == 0) {
				System.out.println('\n' + "=".repeat(80));
				System.out.println("✅ Charts generated successfully!");
				System.out.println("=".repeat(80));
			} else {
				System.err.println('\n' + "=".repeat(80));
				System.err.println(
						"⚠️  Chart generation completed with warnings (exit code: " + exitCode +
								')');
				System.err.println("   This may be due to missing Python dependencies.");
				System.err.println("   Install with: pip3 install pandas matplotlib");
				System.err.println("=".repeat(80));
			}
			
		} catch (IOException e) {
			System.err.println("⚠️  Could not run Python chart generator: " + e.getMessage());
			System.err.println("   Make sure Python 3 is installed and in your PATH");
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			System.err.println("⚠️  Chart generation was interrupted");
		}
	}
	
	/**
	 * Test result data class
	 */
	record TestResult(String endpointName, int concurrentUsers, int successfulRequests,
					  int failedRequests, double durationSeconds, double mean, long min, long max,
					  long p50, long p90, long p95, long p99, double throughput,
					  double successRate) {
	}
}

