package dev.pacr.dns.model.rfc8427;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class to convert between RFC 8427 compliant models and wire format
 *
 * @see <a href="https://tools.ietf.org/html/rfc8427">RFC 8427</a>
 */
public enum DnsMessageConverter {
	;
	
	/**
	 * Create an RFC 8427 compliant DNS query message
	 */
	public static DnsMessage createQuery(String qname, int qtype, int qclass) {
		DnsMessage message = new DnsMessage();
		message.setId(0); // RFC 8484 recommends 0 for DoH
		message.setQr(0); // 0 = Query
		message.setOpcode(0); // 0 = Standard Query
		message.setRd(1); // Recursion Desired
		message.setQdcount(1);
		message.setAncount(0);
		message.setNscount(0);
		message.setArcount(0);
		message.setQname(qname);
		message.setQtype(qtype);
		message.setQclass(qclass);
		return message;
	}
	
	/**
	 * Create an RFC 8427 compliant DNS response message
	 */
	public static DnsMessage createResponse(String qname, int qtype, int qclass, int rcode,
											List<String> answers, long ttl) {
		DnsMessage message = new DnsMessage();
		message.setId(0);
		message.setQr(1); // 1 = Response
		message.setOpcode(0);
		message.setAa(0);
		message.setTc(0);
		message.setRd(1);
		message.setRa(1); // Recursion Available
		message.setAd(0);
		message.setCd(0);
		message.setRcode(rcode);
		message.setQdcount(1);
		message.setQname(qname);
		message.setQtype(qtype);
		message.setQclass(qclass);
		
		if (answers != null && !answers.isEmpty()) {
			message.setAncount(answers.size());
			List<ResourceRecord> answerRRs = new ArrayList<>();
			for (String answer : answers) {
				ResourceRecord rr = new ResourceRecord();
				rr.setName(qname);
				rr.setType(qtype);
				rr.setRclass(qclass);
				rr.setTtl(ttl);
				rr.setRdata(answer);
				answerRRs.add(rr);
			}
			message.setAnswerRRs(answerRRs);
		} else {
			message.setAncount(0);
		}
		
		message.setNscount(0);
		message.setArcount(0);
		
		return message;
	}
	
	/**
	 * Convert query type string to numeric code
	 */
	public static int getQtypeCode(String type) {
		return switch (type.toUpperCase()) {
			case "A" -> 1;
			case "NS" -> 2;
			case "CNAME" -> 5;
			case "SOA" -> 6;
			case "PTR" -> 12;
			case "MX" -> 15;
			case "TXT" -> 16;
			case "AAAA" -> 28;
			case "SRV" -> 33;
			case "CAA" -> 257;
			default -> 1; // Default to A
		};
	}
	
	/**
	 * Convert query type code to string
	 */
	public static String getQtypeString(int code) {
		return switch (code) {
			case 1 -> "A";
			case 2 -> "NS";
			case 5 -> "CNAME";
			case 6 -> "SOA";
			case 12 -> "PTR";
			case 15 -> "MX";
			case 16 -> "TXT";
			case 28 -> "AAAA";
			case 33 -> "SRV";
			case 257 -> "CAA";
			default -> "UNKNOWN";
		};
	}
}

