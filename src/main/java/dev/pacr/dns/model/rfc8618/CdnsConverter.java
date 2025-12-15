package dev.pacr.dns.model.rfc8618;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class to convert DNS transaction data to RFC 8618 C-DNS format
 * <p>
 * C-DNS is designed for efficient storage and analysis of DNS traffic.
 *
 * @see <a href="https://tools.ietf.org/html/rfc8618">RFC 8618</a>
 * @author Patrick Rafferty
 */
public class CdnsConverter {
	
	/**
	 * The ip address index.
	 */
	private final Map<String, Integer> ipAddressIndex = new HashMap<>();
	
	/**
	 * The name index.
	 */
	private final Map<String, Integer> nameIndex = new HashMap<>();
	
	/**
	 * The ip addresses.
	 */
	private final List<String> ipAddresses = new ArrayList<>();
	
	/**
	 * The names.
	 */
	private final List<String> names = new ArrayList<>();
	
	/**
	 * Create a new C-DNS file structure
	 *
	 * @return a new C-DNS file structure
	 */
	public CdnsFile createCdnsFile() {
		CdnsFile file = new CdnsFile();
		
		FilePreamble preamble = new FilePreamble();
		preamble.setMajorFormatVersion(1);
		preamble.setMinorFormatVersion(0);
		
		// Set up block parameters
		BlockParameters blockParams = new BlockParameters();
		
		StorageParameters storageParams = new StorageParameters();
		storageParams.setTicksPerSecond(1000000L); // Microseconds
		storageParams.setMaxBlockItems(10000L);
		
		StorageHints hints = new StorageHints();
		hints.setQueryResponseHints(0xFFL); // Store all data
		storageParams.setStorageHints(hints);
		
		blockParams.setStorageParameters(storageParams);
		
		CollectionParameters collectionParams = new CollectionParameters();
		collectionParams.setGeneratorId("PACR-DNS-Service");
		collectionParams.setHostId(java.net.InetAddress.getLoopbackAddress().getHostName());
		blockParams.setCollectionParameters(collectionParams);
		
		preamble.setBlockParameters(List.of(blockParams));
		file.setFilePreamble(preamble);
		file.setFileBlocks(new ArrayList<>());
		
		return file;
	}
	
	/**
	 * Create a new block for the C-DNS file
	 *
	 * @param earliestTime the earliest time
	 * @return a new block
	 */
	public Block createBlock(Instant earliestTime) {
		Block block = new Block();
		
		BlockPreamble preamble = new BlockPreamble();
		preamble.setEarliestTime(earliestTime.toEpochMilli());
		preamble.setBlockParametersIndex(0);
		block.setBlockPreamble(preamble);
		
		BlockStatistics stats = new BlockStatistics();
		stats.setProcessedMessages(0L);
		stats.setQrDataItems(0L);
		block.setBlockStatistics(stats);
		
		BlockTables tables = new BlockTables();
		tables.setIpAddress(new ArrayList<>());
		tables.setNameRdata(new ArrayList<>());
		tables.setClasstype(new ArrayList<>());
		block.setBlockTables(tables);
		
		block.setQueryResponses(new ArrayList<>());
		
		return block;
	}
	
	/**
	 * Add a query/response transaction to a block
	 *
	 * @param block the block
	 * @param clientIp the client ip
	 * @param queryName the query name
	 * @param qtype the qtype
	 * @param qclass the qclass
	 * @param timestamp the timestamp
	 * @param answers the answers
	 * @param responseTimeMs the response time ms
	 */
	public void addQueryResponse(Block block, String clientIp, String queryName, int qtype,
								 int qclass, Instant timestamp, Collection<String> answers,
								 long responseTimeMs) {
		
		BlockTables tables = block.getBlockTables();
		
		// Get or create index for client IP
		int clientIpIndex = getOrCreateIpIndex(tables, clientIp);
		
		// Get or create index for query name
		int queryNameIdx = getOrCreateNameIndex(tables, queryName);
		
		// Get or create index for classtype
		int classtypeIdx = getOrCreateClassTypeIndex(tables, qtype, qclass);
		
		// Create query response signature
		QueryResponseSignature signature = new QueryResponseSignature();
		signature.setServerPort(53);
		signature.setQueryOpcode(0); // Standard query
		signature.setQueryClasstypeIndex(classtypeIdx);
		signature.setResponseRcode(
				answers != null && !answers.isEmpty() ? 0 : 3); // NOERROR or NXDOMAIN
		
		// Get or create signature index
		List<QueryResponseSignature> signatures = tables.getQrSig();
		if (signatures == null) {
			signatures = new ArrayList<>();
			tables.setQrSig(signatures);
		}
		int sigIndex = signatures.size();
		signatures.add(signature);
		
		// Create query response entry
		QueryResponse qr = new QueryResponse();
		qr.setTimeOffset(timestamp.toEpochMilli() - block.getBlockPreamble().getEarliestTime());
		qr.setClientAddressIndex(clientIpIndex);
		qr.setClientPort(0); // Unknown
		qr.setTransactionId(0);
		qr.setQrSignatureIndex(sigIndex);
		qr.setQueryNameIndex(queryNameIdx);
		qr.setResponseDelay(responseTimeMs * 1000); // Convert to microseconds
		
		block.getQueryResponses().add(qr);
		
		// Update statistics
		BlockStatistics stats = block.getBlockStatistics();
		stats.setProcessedMessages(stats.getProcessedMessages() + 1);
		stats.setQrDataItems(stats.getQrDataItems() + 1);
	}
	
	/**
	 * Get or create ip index.
	 *
	 * @param tables the tables
	 * @param ip the ip
	 * @return the int
	 */
	private int getOrCreateIpIndex(BlockTables tables, String ip) {
		List<String> ipList = tables.getIpAddress();
		int index = ipList.indexOf(ip);
		if (index == -1) {
			index = ipList.size();
			ipList.add(ip);
		}
		return index;
	}
	
	/**
	 * Get or create name index.
	 *
	 * @param tables the tables
	 * @param name the name
	 * @return the int
	 */
	private int getOrCreateNameIndex(BlockTables tables, String name) {
		List<String> nameList = tables.getNameRdata();
		int index = nameList.indexOf(name);
		if (index == -1) {
			index = nameList.size();
			nameList.add(name);
		}
		return index;
	}
	
	/**
	 * Get or create class type index.
	 *
	 * @param tables the tables
	 * @param type the type
	 * @param rclass the rclass
	 * @return the int
	 */
	private int getOrCreateClassTypeIndex(BlockTables tables, int type, int rclass) {
		List<ClassType> classtypes = tables.getClasstype();
		if (classtypes == null) {
			classtypes = new ArrayList<>();
			tables.setClasstype(classtypes);
		}
		
		for (int i = 0; i < classtypes.size(); i++) {
			ClassType ct = classtypes.get(i);
			if (ct.getType().equals(type) && ct.getRclass().equals(rclass)) {
				return i;
			}
		}
		
		int index = classtypes.size();
		classtypes.add(new ClassType(type, rclass));
		return index;
	}
}

