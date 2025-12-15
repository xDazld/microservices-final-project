package dev.pacr.dns.model.rfc8618;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * RFC 8618 C-DNS Collection Parameters
 * <p>
 * Parameters describing how the DNS data was collected.
 *
 * @see <a href="https://tools.ietf.org/html/rfc8618">RFC 8618</a>
 * @author Patrick Rafferty
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CollectionParameters {
	
	/**
	 * The query timeout.
	 */
	@JsonProperty("query-timeout")
	private Long queryTimeout;
	
	/**
	 * The skew timeout.
	 */
	@JsonProperty("skew-timeout")
	private Long skewTimeout;
	
	/**
	 * The snaplen.
	 */
	@JsonProperty("snaplen")
	private Long snaplen;
	
	/**
	 * The promisc.
	 */
	@JsonProperty("promisc")
	private Boolean promisc;
	
	/**
	 * The interfaces.
	 */
	@JsonProperty("interfaces")
	private String[] interfaces;
	
	/**
	 * The server addresses.
	 */
	@JsonProperty("server-addresses")
	private String[] serverAddresses;
	
	/**
	 * The vlan ids.
	 */
	@JsonProperty("vlan-ids")
	private int[] vlanIds;
	
	/**
	 * The filter.
	 */
	@JsonProperty("filter")
	private String filter;
	
	/**
	 * The generator id.
	 */
	@JsonProperty("generator-id")
	private String generatorId;
	
	/**
	 * The host id.
	 */
	@JsonProperty("host-id")
	private String hostId;
	
	// Constructors
	
	/**
	 * Default constructor.
	 */
	public CollectionParameters() {
	}
	
	// Getters and Setters
	
	/**
	 * Gets the query timeout.
	 *
	 * @return the queryTimeout
	 */
	public Long getQueryTimeout() {
		return queryTimeout;
	}
	
	/**
	 * Sets the query timeout.
	 *
	 * @param queryTimeout the queryTimeout to set
	 */
	public void setQueryTimeout(Long queryTimeout) {
		this.queryTimeout = queryTimeout;
	}
	
	/**
	 * Gets the skew timeout.
	 *
	 * @return the skewTimeout
	 */
	public Long getSkewTimeout() {
		return skewTimeout;
	}
	
	/**
	 * Sets the skew timeout.
	 *
	 * @param skewTimeout the skewTimeout to set
	 */
	public void setSkewTimeout(Long skewTimeout) {
		this.skewTimeout = skewTimeout;
	}
	
	/**
	 * Gets the snaplen.
	 *
	 * @return the snaplen
	 */
	public Long getSnaplen() {
		return snaplen;
	}
	
	/**
	 * Sets the snaplen.
	 *
	 * @param snaplen the snaplen to set
	 */
	public void setSnaplen(Long snaplen) {
		this.snaplen = snaplen;
	}
	
	/**
	 * Gets the promisc.
	 *
	 * @return the promisc
	 */
	public Boolean getPromisc() {
		return promisc;
	}
	
	/**
	 * Sets the promisc.
	 *
	 * @param promisc the promisc to set
	 */
	public void setPromisc(Boolean promisc) {
		this.promisc = promisc;
	}
	
	/**
	 * Gets the interfaces.
	 *
	 * @return the interfaces
	 */
	public String[] getInterfaces() {
		return interfaces;
	}
	
	/**
	 * Sets the interfaces.
	 *
	 * @param interfaces the interfaces to set
	 */
	public void setInterfaces(String[] interfaces) {
		this.interfaces = interfaces;
	}
	
	/**
	 * Gets the server addresses.
	 *
	 * @return the serverAddresses
	 */
	public String[] getServerAddresses() {
		return serverAddresses;
	}
	
	/**
	 * Sets the server addresses.
	 *
	 * @param serverAddresses the serverAddresses to set
	 */
	public void setServerAddresses(String[] serverAddresses) {
		this.serverAddresses = serverAddresses;
	}
	
	/**
	 * Gets the vlan ids.
	 *
	 * @return the vlanIds
	 */
	public int[] getVlanIds() {
		return vlanIds;
	}
	
	/**
	 * Sets the vlan ids.
	 *
	 * @param vlanIds the vlanIds to set
	 */
	public void setVlanIds(int[] vlanIds) {
		this.vlanIds = vlanIds;
	}
	
	/**
	 * Gets the filter.
	 *
	 * @return the filter
	 */
	public String getFilter() {
		return filter;
	}
	
	/**
	 * Sets the filter.
	 *
	 * @param filter the filter to set
	 */
	public void setFilter(String filter) {
		this.filter = filter;
	}
	
	/**
	 * Gets the generator id.
	 *
	 * @return the generatorId
	 */
	public String getGeneratorId() {
		return generatorId;
	}
	
	/**
	 * Sets the generator id.
	 *
	 * @param generatorId the generatorId to set
	 */
	public void setGeneratorId(String generatorId) {
		this.generatorId = generatorId;
	}
	
	/**
	 * Gets the host id.
	 *
	 * @return the hostId
	 */
	public String getHostId() {
		return hostId;
	}
	
	/**
	 * Sets the host id.
	 *
	 * @param hostId the hostId to set
	 */
	public void setHostId(String hostId) {
		this.hostId = hostId;
	}
}
