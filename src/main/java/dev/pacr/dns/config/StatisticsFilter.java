package dev.pacr.dns.config;

import dev.pacr.dns.service.EndpointStatisticsService;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

/**
 * JAX-RS filter for recording endpoint usage statistics
 * <p>
 * This filter intercepts all REST requests and responses to measure: - Response
 * time - HTTP status
 * codes - Request/response sizes
 * <p>
 * Statistics are recorded per endpoint and can be accessed via the admin
 * endpoint.
 */
@Provider
public class StatisticsFilter implements ContainerRequestFilter, ContainerResponseFilter {

	private static final Logger LOG = Logger.getLogger(StatisticsFilter.class);
	// Thread-local storage for request start time
	private static final ThreadLocal<Long> REQUEST_START_TIME = new ThreadLocal<>();
	@Inject
	EndpointStatisticsService statisticsService;
	@Context
	HttpServerRequest request;
	@Context
	HttpServerResponse response;

	@Override
	public void filter(ContainerRequestContext requestContext) {
		// Record the start time of the request
		REQUEST_START_TIME.set(System.currentTimeMillis());

		LOG.debugf("Request intercepted: %s %s", requestContext.getMethod(),
				requestContext.getUriInfo().getPath());
	}

	@Override
	public void filter(ContainerRequestContext requestContext,
			ContainerResponseContext responseContext) {
		try {
			// Skip recording for admin/reset endpoints to avoid polluting statistics
			String endpoint = requestContext.getUriInfo().getPath();
			if (endpoint.contains("/api/v1/admin/endpoints/statistics/reset")) {
				return;
			}

			// Calculate response time
			Long startTime = REQUEST_START_TIME.get();
			if (startTime == null) {
				LOG.warnf("Start time not found for request: %s %s", requestContext.getMethod(),
						requestContext.getUriInfo().getPath());
				return;
			}

			long responseTime = System.currentTimeMillis() - startTime;

			// Get request/response details
			String method = requestContext.getMethod();
			int statusCode = responseContext.getStatus();

			// Estimate bytes (in a real application, you'd measure these more accurately)
			long bytesIn = estimateRequestSize(requestContext);
			long bytesOut = estimateResponseSize(responseContext);

			// Record the statistics
			statisticsService.recordRequest(method, endpoint, responseTime, statusCode, bytesIn,
					bytesOut);

			LOG.debugf("Response recorded: %s %s - %d ms - Status %d", method, endpoint,
					responseTime, statusCode);

		} catch (Exception e) {
			LOG.warnf(e, "Error recording statistics for request");
		} finally {
			// Clean up thread-local
			REQUEST_START_TIME.remove();
		}
	}

	/**
	 * Estimate the size of the request body
	 */
	private long estimateRequestSize(ContainerRequestContext requestContext) {
		try {
			// Try to get Content-Length header
			String contentLength = requestContext.getHeaderString("Content-Length");
			if (contentLength != null && !contentLength.isEmpty()) {
				return Long.parseLong(contentLength);
			}
		} catch (NumberFormatException e) {
			LOG.debugf("Could not parse Content-Length: %s", e.getMessage());
		}

		// Default estimate: small value for requests without body
		return 100;
	}

	/**
	 * Estimate the size of the response body
	 */
	private long estimateResponseSize(ContainerResponseContext responseContext) {
		try {
			// Try to get Content-Length header if set
			Object contentLengthObj = responseContext.getHeaders().getFirst("Content-Length");
			if (contentLengthObj != null) {
				return Long.parseLong(contentLengthObj.toString());
			}
		} catch (NumberFormatException e) {
			LOG.debugf("Could not parse Content-Length from response: %s", e.getMessage());
		}

		// Estimate based on entity size if available
		if (responseContext.hasEntity()) {
			Object entity = responseContext.getEntity();
			if (entity != null) {
				// This is a rough estimate
				String entityStr = entity.toString();
				return entityStr.length();
			}
		}

		// Default estimate
		return 500;
	}
}
