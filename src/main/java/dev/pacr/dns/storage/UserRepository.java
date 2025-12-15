package dev.pacr.dns.storage;

import dev.pacr.dns.storage.model.User;
import io.quarkus.mongodb.panache.PanacheMongoRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Repository for persisting users to MongoDB
 *
 * @author Patrick Rafferty
 */
@ApplicationScoped
public class UserRepository implements PanacheMongoRepositoryBase<User, String> {
	
	/**
	 * Find a user by username
	 *
	 * @param username the username
	 * @return the user or null if not found
	 */
	public User findByUsername(String username) {
		return find("username", username).firstResult();
	}
	
	/**
	 * Check if a user exists by username
	 *
	 * @param username the username
	 * @return true if user exists, false otherwise
	 */
	public boolean existsByUsername(String username) {
		return find("username", username).count() > 0;
	}
}

