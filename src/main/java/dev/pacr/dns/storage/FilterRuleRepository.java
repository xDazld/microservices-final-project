package dev.pacr.dns.storage;

import dev.pacr.dns.storage.model.FilterRule;
import io.quarkus.mongodb.panache.PanacheMongoRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import org.bson.types.ObjectId;

import java.util.List;

/**
 * Repository for persisting DNS filter rules to MongoDB
 *
 * @author Patrick Rafferty
 */
@ApplicationScoped
public class FilterRuleRepository implements PanacheMongoRepositoryBase<FilterRule, ObjectId> {
	
	/**
	 * Find a rule by its ruleId (UUID)
	 *
	 * @param ruleId the rule ID
	 * @return the filter rule or null if not found
	 */
	public FilterRule findByRuleId(String ruleId) {
		return find("ruleId", ruleId).firstResult();
	}
	
	/**
	 * Find all enabled rules sorted by priority (descending)
	 *
	 * @return list of enabled rules
	 */
	public List<FilterRule> findEnabledRulesSortedByPriority() {
		return find("enabled", true).page(io.quarkus.panache.common.Page.ofSize(1000)).list()
				.stream().sorted((r1, r2) -> Integer.compare(r2.priority, r1.priority)).toList();
	}
	
	/**
	 * Find rules by category
	 *
	 * @param category the category
	 * @return list of rules in the category
	 */
	public List<FilterRule> findByCategory(String category) {
		return find("category", category).list();
	}
	
	/**
	 * Delete a rule by its ruleId
	 *
	 * @param ruleId the rule ID
	 * @return true if deleted, false if not found
	 */
	public boolean deleteByRuleId(String ruleId) {
		FilterRule rule = findByRuleId(ruleId);
		if (rule != null) {
			delete(rule);
			return true;
		}
		return false;
	}
	
	/**
	 * Count enabled rules
	 *
	 * @return number of enabled rules
	 */
	public long countEnabled() {
		return count("enabled", true);
	}
}

