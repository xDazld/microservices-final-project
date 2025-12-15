package dev.pacr.dns.config;

import io.quarkus.runtime.Startup;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Configuration class for DNS Shield cache instances. This class ensures cache names are registered
 * at startup.
 */
@Startup
@ApplicationScoped
public enum CacheConfiguration {
	;
	
	/**
	 * DNS response cache name (positive cache).
	 */
	public static final String DNS_RESPONSE_CACHE = "dns-response-cache";
	
	/**
	 * DNS negative cache name (failure cache).
	 */
	public static final String DNS_NEGATIVE_CACHE = "dns-negative-cache";
}

