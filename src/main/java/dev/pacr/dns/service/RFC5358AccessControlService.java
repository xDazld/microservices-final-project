package dev.pacr.dns.service;

import io.micrometer.core.instrument.MeterRegistry;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import org.jspecify.annotations.NonNull;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * RFC 5358 Compliance Service - Prevents use of recursive nameservers in reflector attacks.
 * <p>
 * RFC 5358 (BCP 140) Best Current Practice recommendations: 1. Recursive nameservers SHOULD NOT
 * offer recursive service to external networks by default 2. Use IP address based authorization
 * through Access Control Lists (ACLs) 3. Restrict recursive service to only intended clients 4.
 * Separate recursive and authoritative services where practical
 * <p>
 * This service implements IP-based access control to prevent DNS amplification attacks where open
 * recursive nameservers are used as reflectors in DDoS attacks.
 *
 * @see <a href="https://tools.ietf.org/html/rfc5358">RFC 5358 - Preventing Use of Recursive
 * Nameservers in Reflector Attacks</a>
 */
@ApplicationScoped
public class RFC5358AccessControlService {
	
	private static final Logger LOG = Logger.getLogger(RFC5358AccessControlService.class);
	
	// RFC 5358 Section 4: IP address based authorization through ACLs
	private final Set<String> allowedNetworks = ConcurrentHashMap.newKeySet();
	private final Set<String> allowedHosts = ConcurrentHashMap.newKeySet();
	private final Set<String> deniedHosts = ConcurrentHashMap.newKeySet();
	
	@Inject
	MeterRegistry registry;
	
	@ConfigProperty(name = "dns.rfc5358.recursion-enabled", defaultValue = "true")
	boolean recursionEnabled;
	
	@ConfigProperty(name = "dns.rfc5358.default-deny", defaultValue = "false")
	boolean defaultDeny;
	
	@ConfigProperty(name = "dns.rfc5358.allowed-networks", defaultValue = "127.0.0.0/8,10.0.0.0/8," +
			"172.16.0.0/12,192.168.0.0/16,::1/128,fc00::/7")
	List<String> configuredAllowedNetworks;
	
	/**
	 * Initialize the access control service with configured networks.
	 * <p>
	 * RFC 5358 Section 4: "Use the IP source address of the DNS queries and filter them through an
	 * Access Control List (ACL) to service only the intended clients."
	 */
	public void initialize() {
		// Clear and reinitialize
		allowedNetworks.clear();
		allowedHosts.clear();
		deniedHosts.clear();
		
		// Reset recursion to enabled (default from config)
		recursionEnabled = true;
		
		// Add configured allowed networks
		if (configuredAllowedNetworks != null && !configuredAllowedNetworks.isEmpty()) {
			allowedNetworks.addAll(configuredAllowedNetworks);
			LOG.infof("RFC 5358: Initialized with %d allowed networks: %s", allowedNetworks.size(),
					allowedNetworks);
		}
		
		// RFC 5358 Section 4: By default, restrict to local networks
		if (allowedNetworks.isEmpty()) {
			// RFC 1918 private address spaces
			allowedNetworks.add("127.0.0.0/8");      // Loopback
			allowedNetworks.add("10.0.0.0/8");       // Private network
			allowedNetworks.add("172.16.0.0/12");    // Private network
			allowedNetworks.add("192.168.0.0/16");   // Private network
			// IPv6 private ranges
			allowedNetworks.add("::1/128");          // IPv6 loopback
			allowedNetworks.add("fc00::/7");         // IPv6 Unique Local Address
			
			LOG.info("RFC 5358: Using default private network ACL");
		}
		
		LOG.infof("RFC 5358 Access Control initialized - Recursion: %s, Default Policy: %s",
				recursionEnabled ? "ENABLED" : "DISABLED", defaultDeny ? "DENY" : "ALLOW");
	}
	
	/**
	 * Check if a client IP address is authorized for recursive DNS queries.
	 * <p>
	 * RFC 5358 Section 4: "IP address based authorization. Use the IP source address of the DNS
	 * queries and filter them through an Access Control List (ACL) to service only the intended
	 * clients."
	 *
	 * @param clientIp IP address of the client making the DNS query
	 * @return true if the client is authorized, false otherwise
	 */
	public boolean isAuthorized(String clientIp) {
		// RFC 5358 Section 4: If recursion is disabled globally, deny all
		if (!recursionEnabled) {
			LOG.debugf("RFC 5358: Recursion disabled globally, denying %s", clientIp);
			registry.counter("dns.rfc5358.blocked", "reason", "recursion_disabled").increment();
			return false;
		}
		
		// Check explicit deny list first (takes precedence)
		if (deniedHosts.contains(clientIp)) {
			LOG.infof("RFC 5358: Client %s is explicitly denied", clientIp);
			registry.counter("dns.rfc5358.blocked", "reason", "explicit_deny").increment();
			return false;
		}
		
		// Check explicit allow list
		if (allowedHosts.contains(clientIp)) {
			LOG.debugf("RFC 5358: Client %s is explicitly allowed", clientIp);
			registry.counter("dns.rfc5358.allowed", "reason", "explicit_allow").increment();
			return true;
		}
		
		// Check if IP is in allowed networks
		if (isInAllowedNetwork(clientIp)) {
			LOG.debugf("RFC 5358: Client %s is in allowed network", clientIp);
			registry.counter("dns.rfc5358.allowed", "reason", "network_acl").increment();
			return true;
		}
		
		// RFC 5358 Section 4: By default, nameservers SHOULD NOT offer recursive
		// service to external networks
		if (defaultDeny) {
			LOG.infof("RFC 5358: Client %s denied by default policy (external network)", clientIp);
			registry.counter("dns.rfc5358.blocked", "reason", "default_deny").increment();
			return false;
		}
		
		// Allow by default if not in strict mode (for development/testing)
		LOG.warnf(
				"RFC 5358 WARNING: Client %s allowed but not in ACL - consider enabling " +
						"default-deny",
				clientIp);
		registry.counter("dns.rfc5358.allowed", "reason", "permissive_default").increment();
		return true;
	}
	
	/**
	 * Check if an IP address is within any of the allowed networks (CIDR ranges).
	 */
	private boolean isInAllowedNetwork(String clientIp) {
		try {
			InetAddress clientAddr = InetAddress.getByName(clientIp);
			byte[] clientBytes = clientAddr.getAddress();
			
			for (String network : allowedNetworks) {
				if (network.contains("/")) {
					// CIDR notation
					String[] parts = network.split("/");
					String networkIp = parts[0];
					int prefixLength = Integer.parseInt(parts[1]);
					
					try {
						InetAddress networkAddr = InetAddress.getByName(networkIp);
						byte[] networkBytes = networkAddr.getAddress();
						
						// Check if same IP version
						if (clientBytes.length != networkBytes.length) {
							continue;
						}
						
						// Check if client IP is in network range
						if (isInCIDRRange(clientBytes, networkBytes, prefixLength)) {
							return true;
						}
					} catch (UnknownHostException e) {
						LOG.warnf("Invalid network address in ACL: %s", network);
					}
				} else {
					// Single IP address
					if (clientIp.equals(network)) {
						return true;
					}
				}
			}
		} catch (UnknownHostException e) {
			LOG.warnf("Invalid client IP address: %s", clientIp);
			return false;
		}
		
		return false;
	}
	
	/**
	 * Check if an IP address is within a CIDR range.
	 */
	private boolean isInCIDRRange(byte[] ip, byte[] network, int prefixLength) {
		int bytes = prefixLength / 8;
		
		// Check full bytes
		for (int i = 0; i < bytes; i++) {
			if (ip[i] != network[i]) {
				return false;
			}
		}
		
		// Check remaining bits
		int bits = prefixLength % 8;
		if (bits > 0 && bytes < ip.length) {
			int mask = (0xFF << (8 - bits)) & 0xFF;
			return (ip[bytes] & mask) == (network[bytes] & mask);
		}
		
		return true;
	}
	
	/**
	 * Add an IP network to the allowed list.
	 *
	 * @param network CIDR notation (e.g., "192.168.1.0/24") or single IP
	 */
	public void allowNetwork(String network) {
		allowedNetworks.add(network);
		LOG.infof("RFC 5358: Added allowed network: %s", network);
	}
	
	/**
	 * Remove an IP network from the allowed list.
	 */
	public void denyNetwork(String network) {
		allowedNetworks.remove(network);
		LOG.infof("RFC 5358: Removed allowed network: %s", network);
	}
	
	/**
	 * Add a specific IP address to the allowed list.
	 */
	public void allowHost(String ip) {
		allowedHosts.add(ip);
		LOG.infof("RFC 5358: Added allowed host: %s", ip);
	}
	
	/**
	 * Add a specific IP address to the denied list.
	 */
	public void denyHost(String ip) {
		deniedHosts.add(ip);
		LOG.infof("RFC 5358: Added denied host: %s", ip);
	}
	
	/**
	 * Enable or disable recursion globally.
	 * <p>
	 * RFC 5358 Section 4: "In nameservers that do not need to be providing recursive service, for
	 * instance servers that are meant to be authoritative only, turn recursion off completely."
	 */
	public void setRecursionEnabled(boolean enabled) {
		this.recursionEnabled = enabled;
		LOG.infof("RFC 5358: Recursion %s", enabled ? "ENABLED" : "DISABLED");
	}
	
	/**
	 * Get allowed networks for administrative purposes.
	 */
	public Set<String> getAllowedNetworks() {
		return new HashSet<>(allowedNetworks);
	}
	
	/**
	 * Get allowed hosts for administrative purposes.
	 */
	public Set<String> getAllowedHosts() {
		return new HashSet<>(allowedHosts);
	}
	
	/**
	 * Get denied hosts for administrative purposes.
	 */
	public Set<String> getDeniedHosts() {
		return new HashSet<>(deniedHosts);
	}
	
	/**
	 * Get RFC 5358 compliance status.
	 */
	public RFC5358Status getComplianceStatus() {
		return new RFC5358Status(recursionEnabled, defaultDeny, allowedNetworks.size(),
				allowedHosts.size(), deniedHosts.size());
	}
	
	/**
	 * RFC 5358 compliance status information.
	 */
		public record RFC5358Status(boolean recursionEnabled, boolean defaultDeny, int allowedNetworkCount,
									int allowedHostCount, int deniedHostCount) {
		
		/**
		 * Check if configuration follows RFC 5358 best practices.
		 */
			public boolean isCompliant() {
				// RFC 5358: Recursion should be restricted (either disabled or with ACLs)
				if (!recursionEnabled) {
					return true; // Recursion disabled = fully compliant
				}
				
				// If recursion enabled, should have ACLs (networks or default-deny)
				return defaultDeny || allowedNetworkCount > 0;
			}
			
			@Override
			public @NonNull String toString() {
				return String.format(
						"RFC5358Status{recursion=%s, defaultDeny=%s, networks=%d, allowed=%d, " +
								"denied=%d, compliant=%s}", recursionEnabled, defaultDeny,
						allowedNetworkCount, allowedHostCount, deniedHostCount, isCompliant());
			}
		}
}
