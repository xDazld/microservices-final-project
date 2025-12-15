package dev.pacr.dns.storage.model;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import org.bson.codecs.pojo.annotations.BsonProperty;

/**
 * User entity for authentication Stores user credentials securely with password hashing
 *
 * @author Patrick Rafferty
 */
public class User extends PanacheMongoEntity {
	
	/**
	 * The username (unique)
	 */
	@BsonProperty("username")
	public String username;
	
	/**
	 * The hashed password
	 */
	@BsonProperty("password")
	public String password;
	
	/**
	 * The user role (e.g., "admin", "user")
	 */
	@BsonProperty("role")
	public String role;
	
	/**
	 * Default constructor
	 */
	public User() {
	}
	
	/**
	 * Constructor with fields
	 *
	 * @param username the username
	 * @param password the hashed password
	 * @param role     the user role
	 */
	public User(String username, String password, String role) {
		this.username = username;
		this.password = password;
		this.role = role;
	}
}

