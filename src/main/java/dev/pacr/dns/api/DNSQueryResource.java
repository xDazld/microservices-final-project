package dev.pacr.dns.api;

import dev.pacr.dns.DNSResponseCodes;
import dev.pacr.dns.model.rfc8427.DnsMessage;
import dev.pacr.dns.model.rfc8427.DnsMessageConverter;
import dev.pacr.dns.service.DNSOrchestrator;
import dev.pacr.dns.service.RFC5358AccessControlService;
import io.vertx.core.http.HttpServerRequest;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;

import java.io.DataOutput;
import java.io.IOException;
import java.util.Base64;

/**
 * DNS over HTTP (DoH) endpoint compliant with RFC 8484 and RFC 5358
 *
 * This endpoint provides DNS resolution over HTTP with support for:
 * - GET requests with base64url encoded DNS messages
 * - POST requests with DNS wire format binary messages
 * - Media type: application/dns-message
 * - RFC 5358 access control to prevent use as reflector in DDoS attacks
 *
 * @author Patrick Rafferty
 * @see <a href="https://tools.ietf.org/html/rfc8484">RFC 8484 - DNS Queries over HTTPS (DoH)</a>
 * @see
 * <a href="https://tools.ietf.org/html/rfc5358">RFC 5358 - Preventing Use of Recursive Nameservers in Reflector Attacks</a>
 */
@Path("/dns-query")
public class DNSQueryResource {
	
	/** RFC 8484 media type for DNS messages */
	public static final String APPLICATION_DNS_MESSAGE = "application/dns-message";
	/**
	 * Logger for this class
	 */
	private static final Logger LOG = Logger.getLogger(DNSQueryResource.class);
	/** Minimum length of a valid DNS message in bytes */
	private static final int MIN_DNS_MESSAGE_LENGTH = 12;
	
	/** Byte mask for bitwise operations */
	private static final int BYTE_MASK = 0xFF;
	
	/** DNS compression mask for detecting compressed labels */
	private static final int DNS_COMPRESSION_MASK = 0xC0;
	
	/** Offset of QDCOUNT field in DNS header */
	private static final int QDCOUNT_OFFSET = 4;
	
	/** Length of DNS header in bytes */
	private static final int DNS_HEADER_LENGTH = 12;
	
	/** Length of DNS query type field in bytes */
	private static final int DNS_QUERYTYPE_LENGTH = 2;
	
	/** DNS response flag with QR bit set */
	private static final int FLAG_RESPONSE = 0x8400;
	
	/**
	 * DNS flag mask for RCODE (lower 4 bits)
	 */
	private static final int RCODE_MASK = 0x0F;
	
	/**
	 * DNS flag for QR bit (Query/Response)
	 */
	private static final int FLAG_QR = 0x8000;
	
	/**
	 * DNS flag for RA bit (Recursion Available)
	 */
	private static final int FLAG_RA = 0x0400;
	
	/** DNS query type for A records */
	private static final int QTYPE_A = 1;
	
	/** DNS query type for NS records */
	private static final int QTYPE_NS = 2;
	
	/** DNS query type for CNAME records */
	private static final int QTYPE_CNAME = 5;
	
	/** DNS query type for SOA records */
	private static final int QTYPE_SOA = 6;
	
	/** DNS query type for PTR records */
	private static final int QTYPE_PTR = 12;
	
	/** DNS query type for HINFO records */
	private static final int QTYPE_HINFO = 13;
	
	/** DNS query type for MX records */
	private static final int QTYPE_MX = 15;
	
	/** DNS query type for TXT records */
	private static final int QTYPE_TXT = 16;
	
	/** DNS query type for AAAA records */
	private static final int QTYPE_AAAA = 28;
	
	/** DNS query type for SRV records */
	private static final int QTYPE_SRV = 33;
	
	/** DNS query type for OPT records */
	private static final int QTYPE_OPT = 41;
	
	/** DNS query type for DNSKEY records */
	private static final int QTYPE_DNSKEY = 48;
	
	/** DNS query type for CAA records */
	private static final int QTYPE_CAA = 257;
	
	/**
	 * Maximum length of a DNS label in bytes
	 */
	private static final int MAX_DNS_LABEL_LENGTH = 63;
	
	/**
	 * Maximum length of a domain name in bytes (excluding null terminator)
	 */
	private static final int MAX_DOMAIN_LENGTH = 253;
	
	/** DNS Orchestrator service for processing DNS queries */
	@Inject
	DNSOrchestrator orchestrator;
	
	/** RFC 5358 access control service */
	@Inject
	RFC5358AccessControlService accessControl;
	
	/** HTTP server request context */
	@Context
	HttpServerRequest request;
	
	/**
	 * Handle GET requests with base64url encoded DNS messages
	 *
	 * According to RFC 8484 Section 4.1.1, the DNS query is encoded with base64url
	 * and passed as the "dns" query parameter.
	 *
	 * RFC 8484 Section 4.1 recommends using DNS ID of 0 in requests for HTTP cache
	 * friendliness, since HTTP correlates request and response.
	 *
	 * RFC 5358: Access control is enforced to prevent use as reflector in amplification attacks.
	 *
	 * @param dnsParam base64url encoded DNS message
	 * @return DNS response in wire format with application/dns-message media type
	 */
	@GET
	@Produces(APPLICATION_DNS_MESSAGE)
	public Response getQuery(@QueryParam("dns") String dnsParam) {
		String clientIp = getClientIpAddress();
		LOG.infof("Received DoH GET request from %s", clientIp);
		
		// RFC 5358 Section 4: IP address based authorization
		if (!accessControl.isAuthorized(clientIp)) {
			LOG.warnf("RFC 5358: Unauthorized DNS query from %s - REFUSED", clientIp);
			// Return DNS REFUSED response per RFC 5358
			byte[] refusedResponse = createDNSErrorResponse(0, DNSResponseCodes.REFUSED);
			return Response.ok(refusedResponse, APPLICATION_DNS_MESSAGE)
					.header("Cache-Control", "no-cache").build();
		}
		
		if (dnsParam == null || dnsParam.isEmpty()) {
			LOG.warn("Missing dns query parameter in GET request");
			return Response.status(Response.Status.BAD_REQUEST)
					.entity("Missing 'dns' query parameter").build();
		}
		
		try {
			// Decode base64url to get the DNS wire format message
			byte[] dnsMessage = base64urlDecode(dnsParam);
			
			// Process the DNS message
			return processDNSMessage(dnsMessage, clientIp);
		} catch (IllegalArgumentException e) {
			LOG.warnf("Invalid base64url encoding in dns parameter: %s", e.getMessage());
			return Response.status(Response.Status.BAD_REQUEST).entity("Invalid base64url " +
							"encoding")
					.build();
		}
	}
	
	/**
	 * Handle POST requests with DNS wire format messages
	 *
	 * According to RFC 8484 Section 4.1, the DNS query is sent as the message body
	 * with Content-Type: application/dns-message.
	 *
	 * RFC 5358: Access control is enforced to prevent use as reflector in amplification attacks.
	 *
	 * @param dnsMessage DNS query in wire format (binary)
	 * @return DNS response in wire format with application/dns-message media type
	 */
	@POST
	@Consumes(APPLICATION_DNS_MESSAGE)
	@Produces(APPLICATION_DNS_MESSAGE)
	public Response postQuery(byte[] dnsMessage) {
		String clientIp = getClientIpAddress();
		
		if (dnsMessage != null) {
			LOG.infof("Received DoH POST request from %s with %d byte DNS message", clientIp,
					dnsMessage.length);
		} else {
			LOG.warnf("Received DoH POST request from %s with null DNS message", clientIp);
		}
		
		// RFC 5358 Section 4: IP address based authorization
		if (!accessControl.isAuthorized(clientIp)) {
			LOG.warnf("RFC 5358: Unauthorized DNS query from %s - REFUSED", clientIp);
			// Return DNS REFUSED response per RFC 5358
			byte[] refusedResponse = createDNSErrorResponse(0, DNSResponseCodes.REFUSED);
			return Response.ok(refusedResponse, APPLICATION_DNS_MESSAGE)
					.header("Cache-Control", "no-cache").build();
		}
		
		if (dnsMessage == null || dnsMessage.length == 0) {
			LOG.warn("Empty DNS message in POST request");
			return Response.status(Response.Status.BAD_REQUEST).entity("DNS message is required")
					.build();
		}
		
		try {
			// Process the DNS message
			return processDNSMessage(dnsMessage, clientIp);
		} catch (RuntimeException e) {
			LOG.errorf(e, "Error processing DoH POST request");
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity("Error processing DNS query").build();
		}
	}
	
	/**
	 * Extract client IP address from HTTP request.
	 * <p>
	 * RFC 5358 Section 4: Use the IP source address of DNS queries for ACL filtering. Handles
	 * X-Forwarded-For header for proxied requests.
	 */
	private String getClientIpAddress() {
		// Check X-Forwarded-For header first (for proxied requests)
		String xForwardedFor = request.getHeader("X-Forwarded-For");
		if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
			// Take the first IP in the chain (original client)
			String[] ips = xForwardedFor.split(",");
			String clientIp = ips[0].trim();
			LOG.debugf("Client IP from X-Forwarded-For: %s", clientIp);
			return clientIp;
		}
		
		// Fall back to remote address
		String remoteAddr = request.remoteAddress().host();
		LOG.debugf("Client IP from remote address: %s", remoteAddr);
		return remoteAddr;
	}
	
	/**
	 * Process a DNS wire format message and return the response
	 * <p>
	 * This method extracts the domain name from the DNS wire format message, processes it through
	 * the DNS orchestrator, and returns the response.
	 * <p>
	 * According to RFC 8484 Section 4.2.1, the response should be sent as a 2xx HTTP status code
	 * with application/dns-message media type, regardless of the DNS response code (SERVFAIL,
	 * NXDOMAIN, etc).
	 *
	 * @param dnsMessage DNS query in wire format (binary)
	 * @param clientIp IP address of the client (for logging and monitoring)
	 * @return HTTP response with DNS wire format message
	 */
	private Response processDNSMessage(byte[] dnsMessage, String clientIp) {
		try {
			// Parse DNS wire format message to extract domain
			// RFC 1035: DNS message structure
			// Offset 0-1: Transaction ID
			// Offset 2-3: Flags
			// Offset 4-5: QDCOUNT (number of questions)
			// Offset 6-7: ANCOUNT (number of answers)
			// Offset 8-9: NSCOUNT (number of authority records)
			// Offset 10-11: ARCOUNT (number of additional records)
			// Offset 12+: Questions section
			
			if (dnsMessage.length < MIN_DNS_MESSAGE_LENGTH) {
				LOG.warn("DNS message too short");
				// RFC 8484 Section 4.2.1: Return DNS error response with 2xx HTTP status
				byte[] errorResponse = createDNSErrorResponse(0, DNSResponseCodes.FORMAT_ERROR);
				return Response.ok(errorResponse, APPLICATION_DNS_MESSAGE)
						.header("Cache-Control", "max-age=300").build();
			}
			
			// Extract transaction ID
			int transactionId = ((dnsMessage[0] & BYTE_MASK) << 8) | (dnsMessage[1] & BYTE_MASK);
			
			// Extract QDCOUNT (number of questions)
			int qdCount = ((dnsMessage[QDCOUNT_OFFSET] & BYTE_MASK) << 8) |
					(dnsMessage[QDCOUNT_OFFSET + 1] & BYTE_MASK);
			
			if (qdCount < 1) {
				LOG.warn("No questions in DNS message");
				// RFC 8484 Section 4.2.1: Return DNS error response with 2xx HTTP status
				byte[] errorResponse =
						createDNSErrorResponse(transactionId, DNSResponseCodes.FORMAT_ERROR);
				return Response.ok(errorResponse, APPLICATION_DNS_MESSAGE)
						.header("Cache-Control", "max-age=300").build();
			}
			
			// Extract domain name and query type from the first question
			int offset = DNS_HEADER_LENGTH;
			StringBuilder domain = new StringBuilder();
			boolean hasLabels = false; // Track if we've parsed at least one label
			
			// Parse domain name (DNS wire format uses length-prefixed labels)
			while (offset < dnsMessage.length) {
				byte length = dnsMessage[offset];
				offset++;
				
				if (length == 0) {
					// RFC 1912: Zero-length label (empty label) detection
					if (!hasLabels) {
						LOG.warn("Domain name starts with empty label");
						// RFC 8484 Section 4.2.1: Return DNS FORMERR with 2xx HTTP status
						byte[] errorResponse = createDNSErrorResponse(transactionId,
								DNSResponseCodes.FORMAT_ERROR);
						return Response.ok(errorResponse, APPLICATION_DNS_MESSAGE)
								.header("Cache-Control", "max-age=300").build();
					}
					// This appears to be the end-of-domain marker
					// But we need to check if there are more label-like bytes following
					// which would indicate an empty label in the middle (e.g., "invalid..domain")
					if (offset < dnsMessage.length - DNS_QUERYTYPE_LENGTH - 2) {
						// Check if the next byte looks like a valid label length (1-63)
						byte nextByte = dnsMessage[offset];
						if (nextByte > 0 && nextByte <= MAX_DNS_LABEL_LENGTH) {
							// This looks like another label following a zero byte - invalid!
							LOG.warnf(
									"Invalid domain: empty label detected (e.g., consecutive " +
											"dots)");
							// RFC 8484 Section 4.2.1: Return DNS FORMERR with 2xx HTTP status
							byte[] errorResponse = createDNSErrorResponse(transactionId,
									DNSResponseCodes.FORMAT_ERROR);
							return Response.ok(errorResponse, APPLICATION_DNS_MESSAGE)
									.header("Cache-Control", "max-age=300").build();
						}
					}
					// This is the proper end-of-domain marker
					break;
				}
				
				if ((length & DNS_COMPRESSION_MASK) == DNS_COMPRESSION_MASK) {
					// This is a pointer (compression), which shouldn't happen at the start
					// but we'll handle it
					offset++; // Skip the pointer
					break;
				}
				
				// Validate label length (RFC 1035: labels must be 1-63 octets)
				if (length < 0 || length > MAX_DNS_LABEL_LENGTH) {
					LOG.warnf("Invalid label length: %d", length);
					// RFC 8484 Section 4.2.1: Return DNS FORMERR with 2xx HTTP status
					byte[] errorResponse =
							createDNSErrorResponse(transactionId, DNSResponseCodes.FORMAT_ERROR);
					return Response.ok(errorResponse, APPLICATION_DNS_MESSAGE)
							.header("Cache-Control", "max-age=300").build();
				}
				
				if (hasLabels) {
					domain.append('.');
				}
				hasLabels = true;
				
				for (int i = 0; i < length; i++) {
					if (offset >= dnsMessage.length) {
						LOG.warn("Invalid domain name encoding in DNS message");
						// RFC 8484 Section 4.2.1: Return DNS FORMERR with 2xx HTTP status
						byte[] errorResponse = createDNSErrorResponse(transactionId,
								DNSResponseCodes.FORMAT_ERROR);
						return Response.ok(errorResponse, APPLICATION_DNS_MESSAGE)
								.header("Cache-Control", "max-age=300").build();
					}
					domain.append((char) dnsMessage[offset]);
					offset++;
				}
			}
			
			// RFC 1035 validation: Check domain name length constraints
			// Total domain length (including null terminator) must be <= 255 octets
			if (domain.length() > MAX_DOMAIN_LENGTH) {  // 253 + null terminator = 254 octets max
				LOG.warnf("Domain name exceeds maximum length of 255 characters: %s", domain);
				// RFC 8484 Section 4.2.1: Return DNS FORMERR with 2xx HTTP status
				byte[] errorResponse =
						createDNSErrorResponse(transactionId, DNSResponseCodes.FORMAT_ERROR);
				return Response.ok(errorResponse, APPLICATION_DNS_MESSAGE)
						.header("Cache-Control", "max-age=300").build();
			}
			
			// RFC 1035 validation: Check individual label length constraints and character
			// validity
			// Each label must be <= 63 octets and contain only valid DNS characters
			// Valid DNS characters: a-z, A-Z, 0-9, hyphen (but not at start or end)
			String[] labels = domain.toString().split("\\.");
			for (String label : labels) {
				if (label.length() > MAX_DNS_LABEL_LENGTH) {
					LOG.warnf("Domain label exceeds maximum length of 63 characters: %s", label);
					// RFC 8484 Section 4.2.1: Return DNS FORMERR with 2xx HTTP status
					byte[] errorResponse =
							createDNSErrorResponse(transactionId, DNSResponseCodes.FORMAT_ERROR);
					return Response.ok(errorResponse, APPLICATION_DNS_MESSAGE)
							.header("Cache-Control", "max-age=300").build();
				}
				
				// RFC 1035: Check label character validity
				// Labels must contain only alphanumeric characters and hyphens
				// Hyphens cannot be at the start or end of a label
				if (!label.matches("^[a-zA-Z0-9]([a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?$")) {
					LOG.warnf("Domain label contains invalid characters or format: %s", label);
					// RFC 8484 Section 4.2.1: Return DNS FORMERR with 2xx HTTP status
					byte[] errorResponse =
							createDNSErrorResponse(transactionId, DNSResponseCodes.FORMAT_ERROR);
					return Response.ok(errorResponse, APPLICATION_DNS_MESSAGE)
							.header("Cache-Control", "max-age=300").build();
				}
			}
			
			// Extract query type (2 bytes after domain name)
			if (offset + DNS_QUERYTYPE_LENGTH > dnsMessage.length) {
				LOG.warn("Invalid DNS message: missing query type");
				// RFC 8484 Section 4.2.1: Return DNS FORMERR with 2xx HTTP status
				byte[] errorResponse =
						createDNSErrorResponse(transactionId, DNSResponseCodes.FORMAT_ERROR);
				return Response.ok(errorResponse, APPLICATION_DNS_MESSAGE)
						.header("Cache-Control", "max-age=300").build();
			}
			
			int queryType =
					((dnsMessage[offset] & BYTE_MASK) << 8) | (dnsMessage[offset + 1] & BYTE_MASK);
			
			// RFC 3597 Section 2: TYPE0 is reserved and must not be used
			if (queryType == 0) {
				LOG.warnf("Invalid query type 0 (TYPE0) - reserved per RFC 3597");
				// RFC 8484 Section 4.2.1: Return DNS FORMERR with 2xx HTTP status
				byte[] errorResponse =
						createDNSErrorResponse(transactionId, DNSResponseCodes.FORMAT_ERROR);
				return Response.ok(errorResponse, APPLICATION_DNS_MESSAGE)
						.header("Cache-Control", "max-age=300").build();
			}
			
			String queryTypeStr = getQueryTypeString(queryType);
			
			LOG.infof("Parsed DNS query: domain=%s, type=%s (0x%04x)", domain.toString(),
					queryTypeStr, queryType);
			
			// Create RFC 8427 compliant DNS query
			DnsMessage query =
					DnsMessageConverter.createQuery(domain.toString(), queryType, 1); // IN class
			
			// Process the query through the orchestrator
			DnsMessage response = orchestrator.processQuery(query, clientIp);
			
			// Create a DNS wire format response
			// For RFC 8484 compliance, we return a binary DNS message
			byte[] dnsResponseMessage =
					createDNSResponseMessage(transactionId, domain.toString(), queryType,
							response);
			
			// Return the response with proper media type and cache headers
			// RFC 8484 Section 5.1 recommends Cache-Control headers for GET requests
			return Response.ok(dnsResponseMessage, APPLICATION_DNS_MESSAGE)
					.header("Cache-Control", "max-age=300").build();
			
		} catch (RuntimeException e) {
			LOG.errorf(e, "Error processing DNS message");
			// RFC 8484 Section 4.2.1: Return DNS SERVFAIL (2xx HTTP) instead of HTTP 500
			// Extract transaction ID if possible, otherwise use 0
			int transactionId = 0;
			try {
				if (dnsMessage != null && dnsMessage.length >= 2) {
					transactionId =
							((dnsMessage[0] & BYTE_MASK) << 8) | (dnsMessage[1] & BYTE_MASK);
				}
			} catch (RuntimeException ignored) {
				// If we can't extract transaction ID, use 0
			}
			byte[] errorResponse =
					createDNSErrorResponse(transactionId, DNSResponseCodes.SERVER_FAILURE);
			return Response.ok(errorResponse, APPLICATION_DNS_MESSAGE)
					.header("Cache-Control", "max-age=300").build();
		}
	}
	
	/**
	 * Create a DNS error response message per RFC 1035 and RFC 8484
	 * <p>
	 * RFC 8484 Section 4.2.1 requires returning DNS errors as 2xx HTTP status codes with valid DNS
	 * error messages, not HTTP error status codes.
	 *
	 * @param transactionId The transaction ID from the request
	 * @param rcode         The DNS RCODE (response code): 0=NOERROR, 1=FORMERR, 2=SERVFAIL,
	 *                      3=NXDOMAIN
	 * @return DNS error response in wire format
	 */
	private byte[] createDNSErrorResponse(int transactionId, int rcode) {
		try {
			java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
			java.io.DataOutputStream dos = new java.io.DataOutputStream(baos);
			
			// Transaction ID
			dos.writeShort(transactionId);
			
			// Flags: Response bit (0x8000) + RCODE
			int flags = FLAG_QR | (rcode & RCODE_MASK);
			dos.writeShort(flags);
			
			// QDCOUNT, ANCOUNT, NSCOUNT, ARCOUNT all zero
			dos.writeShort(0);
			dos.writeShort(0);
			dos.writeShort(0);
			dos.writeShort(0);
			
			dos.flush();
			return baos.toByteArray();
		} catch (IOException e) {
			LOG.errorf(e, "Error creating DNS error response");
			return new byte[0];
		}
	}
	
	/**
	 * Create a DNS wire format response message
	 *
	 * This is a simplified implementation that creates a minimal valid DNS response.
	 * In a production system, you would use a DNS library like dnsjava for proper encoding.
	 *
	 * @param transactionId The transaction ID from the request
	 * @param domain The domain name
	 * @param queryType The query type
	 * @param response The RFC 8427 compliant DNS response
	 * @return DNS response in wire format
	 */
	private byte[] createDNSResponseMessage(int transactionId, String domain, int queryType,
											DnsMessage response) {
		// This is a minimal DNS response header
		// In production, use a proper DNS library like dnsjava
		// For now, we'll create a response that indicates the status
		
		try {
			// Create a response buffer with enough space for header + minimal response
			java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
			java.io.DataOutputStream dos = new java.io.DataOutputStream(baos);
			
			// Transaction ID
			dos.writeShort(transactionId);
			
			// Flags: Response flag with appropriate codes
			int flags = FLAG_RESPONSE; // Response, recursion available
			if (response.getRcode() != null) {
				int rcode = response.getRcode();
				flags = FLAG_QR | (rcode & RCODE_MASK); // Set QR bit and RCODE
				if (rcode == 0) {
					flags |= FLAG_RA; // Set RA (Recursion Available)
				}
			}
			dos.writeShort(flags);
			
			// QDCOUNT
			dos.writeShort(1);
			
			// ANCOUNT
			int answerCount =
					(response.getAnswerRRs() != null) ? response.getAnswerRRs().size() : 0;
			dos.writeShort(answerCount);
			
			// NSCOUNT
			dos.writeShort(0);
			
			// ARCOUNT
			dos.writeShort(0);
			
			// Write the question section (echo back the question)
			writeDomainName(dos, domain);
			dos.writeShort(queryType);
			dos.writeShort(1); // IN class
			
			// For a complete implementation, you would also write the answer section
			// This is left as a simplified version for now
			
			dos.flush();
			return baos.toByteArray();
		} catch (IOException e) {
			LOG.errorf(e, "Error creating DNS response message");
			return new byte[0];
		}
	}
	
	/**
	 * Write a domain name in DNS wire format
	 * <p>
	 * DNS names are encoded as a series of labels, each prefixed with a length byte, terminated by
	 * a zero-length label. Example: "example.com" becomes: 07 65 78 61 6d 70 6c 65 03 63 6f 6d 00
	 *
	 * @param dos    DataOutputStream to write to
	 * @param domain Domain name to encode
	 * @throws IOException if writing to the stream fails
	 */
	private void writeDomainName(DataOutput dos, String domain) throws IOException {
		String[] labels = domain.split("\\.");
		for (String label : labels) {
			dos.writeByte(label.length());
			for (char c : label.toCharArray()) {
				dos.writeByte((byte) c);
			}
		}
		dos.writeByte(0); // End of domain name
	}
	
	/**
	 * Convert a DNS query type code to string representation
	 *
	 * @param type DNS query type code
	 * @return String representation of the query type
	 */
	private String getQueryTypeString(int type) {
		return switch (type) {
			case QTYPE_A -> "A";
			case QTYPE_NS -> "NS";
			case QTYPE_CNAME -> "CNAME";
			case QTYPE_SOA -> "SOA";
			case QTYPE_PTR -> "PTR";
			case QTYPE_HINFO -> "HINFO";
			case QTYPE_MX -> "MX";
			case QTYPE_TXT -> "TXT";
			case QTYPE_AAAA -> "AAAA";
			case QTYPE_SRV -> "SRV";
			case QTYPE_OPT -> "OPT";
			case QTYPE_DNSKEY -> "DNSKEY";
			case QTYPE_CAA -> "CAA";
			default -> "UNKNOWN(" + type + ')';
		};
	}
	
	/**
	 * Decode a base64url encoded string to bytes
	 *
	 * According to RFC 4648, base64url uses '-' and '_' instead of '+' and '/',
	 * and padding ('=') is omitted.
	 *
	 * @param input Base64url encoded string
	 * @return Decoded bytes
	 */
	private byte[] base64urlDecode(String input) {
		// Add back the padding if needed
		String paddedInput = input;
		int padding = 4 - (input.length() % 4);
		if (padding != 4) {
			paddedInput = input + "=".repeat(padding);
		}
		
		// Replace base64url characters with standard base64 characters
		paddedInput = paddedInput.replace('-', '+').replace('_', '/');
		
		// Decode using standard Base64 decoder
		return Base64.getDecoder().decode(paddedInput);
	}
}
