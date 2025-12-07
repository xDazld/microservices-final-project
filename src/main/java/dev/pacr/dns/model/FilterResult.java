package dev.pacr.dns.model;

/**
 * Result of applying filtering rules to a DNS query
 */
public class FilterResult {
	
	private boolean blocked;
	private String reason;
	private FilterRule matchedRule;
	private String redirectTo;
	private FilterAction action;
	
	public FilterResult() {
		this.blocked = false;
		this.action = FilterAction.ALLOW;
	}
	
	public static FilterResult allow() {
		FilterResult result = new FilterResult();
		result.setAction(FilterAction.ALLOW);
		result.setBlocked(false);
		return result;
	}
	
	public static FilterResult block(String reason, FilterRule rule) {
		FilterResult result = new FilterResult();
		result.setAction(FilterAction.BLOCK);
		result.setBlocked(true);
		result.setReason(reason);
		result.setMatchedRule(rule);
		return result;
	}
	
	public static FilterResult redirect(String redirectTo, FilterRule rule) {
		FilterResult result = new FilterResult();
		result.setAction(FilterAction.REDIRECT);
		result.setBlocked(false);
		result.setRedirectTo(redirectTo);
		result.setMatchedRule(rule);
		return result;
	}
	
	public boolean isBlocked() {
		return blocked;
	}
	
	// Getters and Setters
	
	public void setBlocked(boolean blocked) {
		this.blocked = blocked;
	}
	
	public String getReason() {
		return reason;
	}
	
	public void setReason(String reason) {
		this.reason = reason;
	}
	
	public FilterRule getMatchedRule() {
		return matchedRule;
	}
	
	public void setMatchedRule(FilterRule matchedRule) {
		this.matchedRule = matchedRule;
	}
	
	public String getRedirectTo() {
		return redirectTo;
	}
	
	public void setRedirectTo(String redirectTo) {
		this.redirectTo = redirectTo;
	}
	
	public FilterAction getAction() {
		return action;
	}
	
	public void setAction(FilterAction action) {
		this.action = action;
	}
	
	@Override
	public String toString() {
		return "FilterResult{" + "blocked=" + blocked + ", reason='" + reason + '\'' +
				", matchedRule=" + (matchedRule != null ? matchedRule.getName() : "none") +
				", redirectTo='" + redirectTo + '\'' + ", action=" + action + '}';
	}
	
	public enum FilterAction {
		ALLOW, BLOCK, REDIRECT
	}
}
