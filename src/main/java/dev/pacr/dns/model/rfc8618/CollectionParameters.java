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
	
	/**
	  * Constructs a new CollectionParameters.
	  */
	public CollectionParameters() {
	}
	
	// Getters and Setters
	
	/**
	  * Gets the QueryTimeout.
	  * @return the QueryTimeout
	  */
	public Long getQueryTimeout() {
		return queryTimeout;
	}
	
	/**
	  * Sets the QueryTimeout.
	  * @param queryTimeout the QueryTimeout to set
	  */
	public void setQueryTimeout(Long queryTimeout) {
		this.queryTimeout = queryTimeout;
	}
	
	/**
	  * Gets the SkewTimeout.
	  * @return the SkewTimeout
	  */
	public Long getSkewTimeout() {
		return skewTimeout;
	}
	
	/**
	  * Sets the SkewTimeout.
	  * @param skewTimeout the SkewTimeout to set
	  */
	public void setSkewTimeout(Long skewTimeout) {
		this.skewTimeout = skewTimeout;
	}
	
	/**
	  * Gets the Snaplen.
	  * @return the Snaplen
	  */
	public Long getSnaplen() {
		return snaplen;
	}
	
	/**
	  * Sets the Snaplen.
	  * @param snaplen the Snaplen to set
	  */
	public void setSnaplen(Long snaplen) {
		this.snaplen = snaplen;
	}
	
	/**
	  * Gets the Promisc.
	  * @return the Promisc
	  */
	public Boolean getPromisc() {
		return promisc;
	}
	
	/**
	  * Sets the Promisc.
	  * @param promisc the Promisc to set
	  */
	public void setPromisc(Boolean promisc) {
		this.promisc = promisc;
	}
	
	/**
	  * Gets the Interfaces.
	  * @return the Interfaces
	  */
	public String[] getInterfaces() {
		return interfaces;
	}
	
	/**
	  * Sets the Interfaces.
	  * @param interfaces the Interfaces to set
	  */
	public void setInterfaces(String[] interfaces) {
		this.interfaces = interfaces;
	}
	
	/**
	  * Gets the ServerAddresses.
	  * @return the ServerAddresses
	  */
	public String[] getServerAddresses() {
		return serverAddresses;
	}
	
	/**
	  * Sets the ServerAddresses.
	  * @param serverAddresses the ServerAddresses to set
	  */
	public void setServerAddresses(String[] serverAddresses) {
		this.serverAddresses = serverAddresses;
	}
	
	/**
	  * Gets the VlanIds.
	  * @return the VlanIds
	  */
	public int[] getVlanIds() {
		return vlanIds;
	}
	
	/**
	  * Sets the VlanIds.
	  * @param vlanIds the VlanIds to set
	  */
	public void setVlanIds(int[] vlanIds) {
		this.vlanIds = vlanIds;
	}
	
	/**
	  * Gets the Filter.
	  * @return the Filter
	  */
	public String getFilter() {
		return filter;
	}
	
	/**
	  * Sets the Filter.
	  * @param filter the Filter to set
	  */
	public void setFilter(String filter) {
		this.filter = filter;
	}
	
	/**
	  * Gets the GeneratorId.
	  * @return the GeneratorId
	  */
	public String getGeneratorId() {
		return generatorId;
	}
	
	/**
	  * Sets the GeneratorId.
	  * @param generatorId the GeneratorId to set
	  */
	public void setGeneratorId(String generatorId) {
		this.generatorId = generatorId;
	}
	
	/**
	  * Gets the HostId.
	  * @return the HostId
	  */
	public String getHostId() {
		return hostId;
	}
	
	/**
	  * Sets the HostId.
	  * @param hostId the HostId to set
	  */
	public void setHostId(String hostId) {
		this.hostId = hostId;
	}
}

