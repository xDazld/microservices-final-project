package dev.pacr.dns.model;

/**
	  * Result of applying filtering rules to a DNS query
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
	  * The matchedRule.
	  */
	private FilterRule matchedRule;
	/**
	  * The redirectTo.
	  */
	private String redirectTo;
	/**
	  * The action.
	  */
	private FilterAction action;
	
	/**
	  * Constructs a new FilterResult.
	  */
	public FilterResult() {
		this.blocked = false;
		this.action = FilterAction.ALLOW;
	}
	
	/**
	  * allow method.
	  */
	public static FilterResult allow() {
		FilterResult result = new FilterResult();
		result.setAction(FilterAction.ALLOW);
		result.setBlocked(false);
		return result;
	}
	
	/**
	  * block method.
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
	  * redirect method.
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
	  * Checks if Blocked.
	  * @return true if Blocked, false otherwise
	  */
	public boolean isBlocked() {
		return blocked;
	}
	
	// Getters and Setters
	
	/**
	  * Sets the Blocked.
	  * @param blocked the Blocked to set
	  */
	public void setBlocked(boolean blocked) {
		this.blocked = blocked;
	}
	
	/**
	  * Gets the Reason.
	  * @return the Reason
	  */
	public String getReason() {
		return reason;
	}
	
	/**
	  * Sets the Reason.
	  * @param reason the Reason to set
	  */
	public void setReason(String reason) {
		this.reason = reason;
	}
	
	/**
	  * Gets the MatchedRule.
	  * @return the MatchedRule
	  */
	public FilterRule getMatchedRule() {
		return matchedRule;
	}
	
	/**
	  * Sets the MatchedRule.
	  * @param matchedRule the MatchedRule to set
	  */
	public void setMatchedRule(FilterRule matchedRule) {
		this.matchedRule = matchedRule;
	}
	
	/**
	  * Gets the RedirectTo.
	  * @return the RedirectTo
	  */
	public String getRedirectTo() {
		return redirectTo;
	}
	
	/**
	  * Sets the RedirectTo.
	  * @param redirectTo the RedirectTo to set
	  */
	public void setRedirectTo(String redirectTo) {
		this.redirectTo = redirectTo;
	}
	
	/**
	  * Gets the Action.
	  * @return the Action
	  */
	public FilterAction getAction() {
		return action;
	}
	
	/**
	  * Sets the Action.
	  * @param action the Action to set
	  */
	public void setAction(FilterAction action) {
		this.action = action;
	}
	
	/**
	  * toString method.
	  */
	public String toString() {
		return "FilterResult{" + "blocked=" + blocked + ", reason='" + reason + '\'' +
				", matchedRule=" + (matchedRule != null ? matchedRule.getName() : "none") +
				", redirectTo='" + redirectTo + '\'' + ", action=" + action + '}';
	}
	
	/**
	  * FilterAction enum.
	  */
	public enum FilterAction {
		/**
	  * ALLOW constant.
		  */
	ALLOW, BLOCK, REDIRECT
	}
}
