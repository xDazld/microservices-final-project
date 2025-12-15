package dev.pacr.dns.model;

import dev.pacr.dns.storage.model.FilterRule;

/**
 * Result of applying filtering rules to a DNS query
 *
 * @author Patrick Rafferty
 */
public class FilterResult {
	
	/**
	 * The blocked.
	 */
	private boolean blocked;
	
	/**
	 * The reason.
	 */
	private String reason;
	
	/**
	 * The matched rule.
	 */
	private FilterRule matchedRule;
	
	/**
	 * The redirect to.
	 */
	private String redirectTo;
	
	/**
	 * The action.
	 */
	private FilterAction action;
	
	/**
	 * Default constructor.
	 */
	public FilterResult() {
		this.blocked = false;
		this.action = FilterAction.ALLOW;
	}
	
	/**
	 * Allow.
	 *
	 * @return the filter result
	 */
	public static FilterResult allow() {
		FilterResult result = new FilterResult();
		result.setAction(FilterAction.ALLOW);
		result.setBlocked(false);
		return result;
	}
	
	/**
	 * Block.
	 *
	 * @param reason the reason
	 * @param rule the rule
	 * @return the filter result
	 */
	public static FilterResult block(String reason, FilterRule rule) {
		FilterResult result = new FilterResult();
		result.setAction(FilterAction.BLOCK);
		result.setBlocked(true);
		result.setReason(reason);
		result.setMatchedRule(rule);
		return result;
	}
	
	/**
	 * Redirect.
	 *
	 * @param redirectTo the redirect to
	 * @param rule the rule
	 * @return the filter result
	 */
	public static FilterResult redirect(String redirectTo, FilterRule rule) {
		FilterResult result = new FilterResult();
		result.setAction(FilterAction.REDIRECT);
		result.setBlocked(false);
		result.setRedirectTo(redirectTo);
		result.setMatchedRule(rule);
		return result;
	}
	
	/**
	 * Is blocked.
	 *
	 * @return the boolean
	 */
	public boolean isBlocked() {
		return blocked;
	}
	
	// Getters and Setters
	
	/**
	 * Sets blocked.
	 *
	 * @param blocked the blocked
	 */
	public void setBlocked(boolean blocked) {
		this.blocked = blocked;
	}
	
	/**
	 * Gets reason.
	 *
	 * @return the reason
	 */
	public String getReason() {
		return reason;
	}
	
	/**
	 * Sets reason.
	 *
	 * @param reason the reason
	 */
	public void setReason(String reason) {
		this.reason = reason;
	}
	
	/**
	 * Gets matched rule.
	 *
	 * @return the matched rule
	 */
	public FilterRule getMatchedRule() {
		return matchedRule;
	}
	
	/**
	 * Sets matched rule.
	 *
	 * @param matchedRule the matched rule
	 */
	public void setMatchedRule(FilterRule matchedRule) {
		this.matchedRule = matchedRule;
	}
	
	/**
	 * Gets redirect to.
	 *
	 * @return the redirect to
	 */
	public String getRedirectTo() {
		return redirectTo;
	}
	
	/**
	 * Sets redirect to.
	 *
	 * @param redirectTo the redirect to
	 */
	public void setRedirectTo(String redirectTo) {
		this.redirectTo = redirectTo;
	}
	
	/**
	 * Gets action.
	 *
	 * @return the action
	 */
	public FilterAction getAction() {
		return action;
	}
	
	/**
	 * Sets action.
	 *
	 * @param action the action
	 */
	public void setAction(FilterAction action) {
		this.action = action;
	}
	
	/**
	 * To string.
	 *
	 * @return the string
	 */
	@Override
	public String toString() {
		return "FilterResult{" + "blocked=" + blocked + ", reason='" + reason + '\'' +
				", matchedRule=" + (matchedRule != null ? matchedRule.name : "none") +
				", redirectTo='" + redirectTo + '\'' + ", action=" + action + '}';
	}
	
	/**
	 * The enum Filter action.
	 */
	public enum FilterAction {
		/**
		 * Allow
		 */
		ALLOW,
		/**
		 * Block
		 */
		BLOCK,
		/**
		 * Redirect
		 */
		REDIRECT
	}
}
