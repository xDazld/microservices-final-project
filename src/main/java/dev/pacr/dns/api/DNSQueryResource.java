package dev.pacr.dns.api;

import dev.pacr.dns.model.DNSQuery;
import dev.pacr.dns.model.DNSResponse;
import dev.pacr.dns.service.DNSOrchestrator;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;

import java.io.IOException;
import java.util.Base64;

/**
 * DNS over HTTP (DoH) endpoint compliant with RFC 8484
 *
 * This endpoint provides DNS resolution over HTTP with support for:
 * - GET requests with base64url encoded DNS messages
 * - POST requests with DNS wire format binary messages
 * - Media type: application/dns-message
 *
 * @see <a href="https://tools.ietf.org/html/rfc8484">RFC 8484</a>
 */
@Path("/dns-query")
public class DNSQueryResource {
	
	private static final Logger LOG = Logger.getLogger(DNSQueryResource.class);
	
	// RFC 8484 media type for DNS messages
	public static final String APPLICATION_DNS_MESSAGE = "application/dns-message";
	
	// DNS message structure constants
	private static final int MIN_DNS_MESSAGE_LENGTH = 12;
	private static final int BYTE_MASK = 0xFF;
	private static final int DNS_COMPRESSION_MASK = 0xC0;
	
	// DNS header field offsets
	private static final int QDCOUNT_OFFSET = 4;
	private static final int QDCOUNT_LENGTH = 2;
	private static final int DNS_HEADER_LENGTH = 12;
	private static final int DNS_QUERYTYPE_LENGTH = 2;
	private static final int DNS_CLASS_LENGTH = 2;
	
	// DNS flags for response
	private static final int FLAG_RESPONSE = 0x8400;
	private static final int FLAG_RESPONSE_NXDOMAIN = 0x8403;
	private static final int FLAG_RESPONSE_SERVFAIL = 0x8402;
	
	// DNS query type codes
	private static final int QTYPE_A = 1;
	private static final int QTYPE_NS = 2;
	private static final int QTYPE_CNAME = 5;
	private static final int QTYPE_SOA = 6;
	private static final int QTYPE_PTR = 12;
	private static final int QTYPE_HINFO = 13;
	private static final int QTYPE_MX = 15;
	private static final int QTYPE_TXT = 16;
	private static final int QTYPE_AAAA = 28;
	private static final int QTYPE_SRV = 33;
	private static final int QTYPE_OPT = 41;
	private static final int QTYPE_DNSKEY = 48;
	private static final int QTYPE_CAA = 257;
	
	@Inject
	DNSOrchestrator orchestrator;
	
	/**
	 * Handle GET requests with base64url encoded DNS messages
	 *
	 * According to RFC 8484 Section 4.1.1, the DNS query is encoded with base64url
	 * and passed as the "dns" query parameter.
	 *
	 * @param dnsParam base64url encoded DNS message
	 * @return DNS response in wire format with application/dns-message media type
	 */
	@GET
	@Produces(APPLICATION_DNS_MESSAGE)
	public Response getQuery(@QueryParam("dns") String dnsParam) {
		LOG.infof("Received DoH GET request with dns parameter");
		
		if (dnsParam == null || dnsParam.isEmpty()) {
			LOG.warn("Missing dns query parameter in GET request");
			return Response.status(Response.Status.BAD_REQUEST)
					.entity("Missing 'dns' query parameter").build();
		}
		
		try {
			// Decode base64url to get the DNS wire format message
			byte[] dnsMessage = base64urlDecode(dnsParam);
			
			// Process the DNS message
			return processDNSMessage(dnsMessage);
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
	 * @param dnsMessage DNS query in wire format (binary)
	 * @return DNS response in wire format with application/dns-message media type
	 */
	@POST
	@Consumes(APPLICATION_DNS_MESSAGE)
	@Produces(APPLICATION_DNS_MESSAGE)
	public Response postQuery(byte[] dnsMessage) {
		if (dnsMessage != null) {
			LOG.infof("Received DoH POST request with %d byte DNS message", dnsMessage.length);
		} else {
			LOG.warn("Received DoH POST request with null DNS message");
		}
		
		if (dnsMessage == null || dnsMessage.length == 0) {
			LOG.warn("Empty DNS message in POST request");
			return Response.status(Response.Status.BAD_REQUEST).entity("DNS message is required")
					.build();
		}
		
		try {
			// Process the DNS message
			return processDNSMessage(dnsMessage);
		} catch (RuntimeException e) {
			LOG.errorf(e, "Error processing DoH POST request");
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity("Error processing DNS query").build();
		}
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
	 * @return HTTP response with DNS wire format message
	 */
	private Response processDNSMessage(byte[] dnsMessage) {
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
				return Response.status(Response.Status.BAD_REQUEST)
						.entity("Invalid DNS message format").build();
			}
			
			// Extract transaction ID
			int transactionId = ((dnsMessage[0] & BYTE_MASK) << 8) | (dnsMessage[1] & BYTE_MASK);
			
			// Extract QDCOUNT (number of questions)
			int qdCount = ((dnsMessage[QDCOUNT_OFFSET] & BYTE_MASK) << 8) |
					(dnsMessage[QDCOUNT_OFFSET + 1] & BYTE_MASK);
			
			if (qdCount < 1) {
				LOG.warn("No questions in DNS message");
				return Response.status(Response.Status.BAD_REQUEST)
						.entity("Invalid DNS message: no questions").build();
			}
			
			// Extract domain name and query type from the first question
			int offset = DNS_HEADER_LENGTH;
			StringBuilder domain = new StringBuilder();
			
			// Parse domain name (DNS wire format uses length-prefixed labels)
			while (offset < dnsMessage.length) {
				byte length = dnsMessage[offset];
				offset++;
				
				if (length == 0) {
					// End of domain name
					break;
				}
				
				if ((length & DNS_COMPRESSION_MASK) == DNS_COMPRESSION_MASK) {
					// This is a pointer (compression), which shouldn't happen at the start
					// but we'll handle it
					offset++; // Skip the pointer
					break;
				}
				
				if (!domain.isEmpty()) {
					domain.append('.');
				}
				
				for (int i = 0; i < length; i++) {
					if (offset >= dnsMessage.length) {
						LOG.warn("Invalid domain name encoding in DNS message");
						return Response.status(Response.Status.BAD_REQUEST)
								.entity("Invalid DNS message format").build();
					}
					domain.append((char) dnsMessage[offset]);
					offset++;
				}
			}
			
			// RFC 1035 validation: Check domain name length constraints
			// Total domain length (including null terminator) must be <= 255 octets
			if (domain.length() > 253) {  // 253 + null terminator = 254 octets max
				LOG.warnf("Domain name exceeds maximum length of 255 characters: %s", domain);
				return Response.status(Response.Status.BAD_REQUEST)
						.entity("Domain name exceeds maximum length of 255 characters").build();
			}
			
			// RFC 1035 validation: Check individual label length constraints
			// Each label must be <= 63 octets
			String[] labels = domain.toString().split("\\.");
			for (String label : labels) {
				if (label.length() > 63) {
					LOG.warnf("Domain label exceeds maximum length of 63 characters: %s", label);
					return Response.status(Response.Status.BAD_REQUEST)
							.entity("Domain label exceeds maximum length of 63 characters").build();
				}
			}
			
			// Extract query type (2 bytes after domain name)
			if (offset + DNS_QUERYTYPE_LENGTH > dnsMessage.length) {
				LOG.warn("Invalid DNS message: missing query type");
				return Response.status(Response.Status.BAD_REQUEST)
						.entity("Invalid DNS message format").build();
			}
			
			int queryType =
					((dnsMessage[offset] & BYTE_MASK) << 8) | (dnsMessage[offset + 1] & BYTE_MASK);
			String queryTypeStr = getQueryTypeString(queryType);
			
			LOG.infof("Parsed DNS query: domain=%s, type=%s (0x%04x)", domain.toString(),
					queryTypeStr, queryType);
			
			// Create DNS query
			DNSQuery query = new DNSQuery(domain.toString(), queryTypeStr, "unknown", "DoH");
			
			// Process the query through the orchestrator
			DNSResponse response = orchestrator.processQuery(query);
			
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
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
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
	 * @param response The DNS response
	 * @return DNS response in wire format
	 */
	private byte[] createDNSResponseMessage(int transactionId, String domain, int queryType,
											DNSResponse response) {
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
			// or 1000 0101 0000 0011 (response, recursion available, NXDOMAIN)
			int flags = FLAG_RESPONSE; // Response, recursion available
			if ("BLOCKED".equals(response.getStatus())) {
				flags = FLAG_RESPONSE_NXDOMAIN; // NXDOMAIN response
			} else if (!"ALLOWED".equals(response.getStatus())) {
				flags = FLAG_RESPONSE_SERVFAIL; // SERVFAIL response
			}
			dos.writeShort(flags);
			
			// QDCOUNT
			dos.writeShort(1);
			
			// ANCOUNT
			int answerCount = ("ALLOWED".equals(response.getStatus()) &&
					response.getResolvedAddresses() != null) ?
					response.getResolvedAddresses().size() : 0;
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
	private void writeDomainName(java.io.DataOutputStream dos, String domain) throws IOException {
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
