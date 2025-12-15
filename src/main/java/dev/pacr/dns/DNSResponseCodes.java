package dev.pacr.dns;

/**
 * DNS Response Code Constants
 * <p>
 * Defined in RFC 1035 Section 4.1.1 and RFC 6895
 *
 * @author Patrick Rafferty
 */
public enum DNSResponseCodes {
	;
	
	/**
	 * No Error
	 */
	public static final int NO_ERROR = 0;
	
	/**
	 * Format Error
	 */
	public static final int FORMAT_ERROR = 1;
	
	/**
	 * Server Failure
	 */
	public static final int SERVER_FAILURE = 2;
	
	/**
	 * Name Error (NXDOMAIN)
	 */
	public static final int NXDOMAIN = 3;
	
	/**
	 * Not Implemented
	 */
	public static final int NOT_IMPLEMENTED = 4;
	
	/**
	 * Refused
	 */
	public static final int REFUSED = 5;
	
}
