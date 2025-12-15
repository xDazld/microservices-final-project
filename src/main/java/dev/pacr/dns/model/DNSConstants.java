package dev.pacr.dns.model;

/**
	  * DNS protocol constants and enumerations
  * <p>
  * This class centralizes DNS-related constants used throughout the application for better
  * maintainability and type safety.
  *
  * @author Patrick Rafferty
  */
public enum DNSConstants {
	;
	
	// DNS Header Constants
	/**
	  * Length of DNS header in bytes
	  */
	public static final int DNS_HEADER_LENGTH = 12;
	/**
	  * Offset of QDCOUNT field in DNS header
	  */
	public static final int QDCOUNT_OFFSET = 4;
	/**
	  * DNS compression mask for detecting compressed labels
	  */
	public static final int DNS_COMPRESSION_MASK = 0xC0;
	/**
	  * Length of DNS query type field in bytes
	  */
	public static final int DNS_QUERYTYPE_LENGTH = 2;
	/**
	  * DNS response flag with QR bit set
	  */
	public static final int FLAG_RESPONSE = 0x8400;
	/**
	  * DNS flag mask for RCODE (lower 4 bits)
	  */
	public static final int RCODE_MASK = 0x0F;
	/**
	  * DNS flag for QR bit (Query/Response)
	  */
	public static final int FLAG_QR = 0x8000;
	/**
	  * DNS flag for RA bit (Recursion Available)
	  */
	public static final int FLAG_RA = 0x0400;
	
	// DNS Record Types
	/**
	  * DNS query type for A records
	  */
	public static final int QTYPE_A = 1;
	/**
	  * DNS query type for NS records
	  */
	public static final int QTYPE_NS = 2;
	/**
	  * DNS query type for CNAME records
	  */
	public static final int QTYPE_CNAME = 5;
	/**
	  * DNS query type for SOA records
	  */
	public static final int QTYPE_SOA = 6;
	/**
	  * DNS query type for PTR records
	  */
	public static final int QTYPE_PTR = 12;
	/**
	  * DNS query type for HINFO records
	  */
	public static final int QTYPE_HINFO = 13;
	/**
	  * DNS query type for MX records
	  */
	public static final int QTYPE_MX = 15;
	/**
	  * DNS query type for TXT records
	  */
	public static final int QTYPE_TXT = 16;
	/**
	  * DNS query type for AAAA records
	  */
	public static final int QTYPE_AAAA = 28;
	/**
	  * DNS query type for SRV records
	  */
	public static final int QTYPE_SRV = 33;
	/**
	  * DNS query type for OPT records
	  */
	public static final int QTYPE_OPT = 41;
	/**
	  * DNS query type for DNSKEY records
	  */
	public static final int QTYPE_DNSKEY = 48;
	/**
	  * DNS query type for CAA records
	  */
	public static final int QTYPE_CAA = 257;
	
	// DNS Limits
	/**
	  * Maximum length of a DNS label in bytes
	  */
	public static final int MAX_DNS_LABEL_LENGTH = 63;
	/**
	  * Maximum length of a domain name in bytes (excluding null terminator)
	  */
	public static final int MAX_DOMAIN_LENGTH = 253;
	/**
	  * Minimum length of a valid DNS message in bytes
	  */
	public static final int MIN_DNS_MESSAGE_LENGTH = 12;
	
	// Byte manipulation
	/**
	  * Byte mask for bitwise operations
	  */
	public static final int BYTE_MASK = 0xFF;
	
	// Media types
	/**
	  * RFC 8484 media type for DNS messages
	  */
	public static final String APPLICATION_DNS_MESSAGE = "application/dns-message";
	
	/**
	  * Get the string representation of a DNS record type
	  *
	  * @param type the DNS record type code
	  * @return human-readable string representation
	  */
	public static String getRecordTypeName(int type) {
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
}
