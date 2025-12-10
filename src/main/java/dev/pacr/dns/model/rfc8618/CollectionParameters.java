package dev.pacr.dns.model.rfc8618;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * RFC 8618 C-DNS Collection Parameters
 * <p>
 * Parameters describing how the DNS data was collected.
 *
 * @see <a href="https://tools.ietf.org/html/rfc8618">RFC 8618</a>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CollectionParameters {
	
	@JsonProperty("query-timeout")
	private Long queryTimeout;
	
	@JsonProperty("skew-timeout")
	private Long skewTimeout;
	
	@JsonProperty("snaplen")
	private Long snaplen;
	
	@JsonProperty("promisc")
	private Boolean promisc;
	
	@JsonProperty("interfaces")
	private String[] interfaces;
	
	@JsonProperty("server-addresses")
	private String[] serverAddresses;
	
	@JsonProperty("vlan-ids")
	private int[] vlanIds;
	
	@JsonProperty("filter")
	private String filter;
	
	@JsonProperty("generator-id")
	private String generatorId;
	
	@JsonProperty("host-id")
	private String hostId;
	
	// Constructors
	
	public CollectionParameters() {
	}
	
	// Getters and Setters
	
	public Long getQueryTimeout() {
		return queryTimeout;
	}
	
	public void setQueryTimeout(Long queryTimeout) {
		this.queryTimeout = queryTimeout;
	}
	
	public Long getSkewTimeout() {
		return skewTimeout;
	}
	
	public void setSkewTimeout(Long skewTimeout) {
		this.skewTimeout = skewTimeout;
	}
	
	public Long getSnaplen() {
		return snaplen;
	}
	
	public void setSnaplen(Long snaplen) {
		this.snaplen = snaplen;
	}
	
	public Boolean getPromisc() {
		return promisc;
	}
	
	public void setPromisc(Boolean promisc) {
		this.promisc = promisc;
	}
	
	public String[] getInterfaces() {
		return interfaces;
	}
	
	public void setInterfaces(String[] interfaces) {
		this.interfaces = interfaces;
	}
	
	public String[] getServerAddresses() {
		return serverAddresses;
	}
	
	public void setServerAddresses(String[] serverAddresses) {
		this.serverAddresses = serverAddresses;
	}
	
	public int[] getVlanIds() {
		return vlanIds;
	}
	
	public void setVlanIds(int[] vlanIds) {
		this.vlanIds = vlanIds;
	}
	
	public String getFilter() {
		return filter;
	}
	
	public void setFilter(String filter) {
		this.filter = filter;
	}
	
	public String getGeneratorId() {
		return generatorId;
	}
	
	public void setGeneratorId(String generatorId) {
		this.generatorId = generatorId;
	}
	
	public String getHostId() {
		return hostId;
	}
	
	public void setHostId(String hostId) {
		this.hostId = hostId;
	}
}

