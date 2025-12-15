package dev.pacr.dns.storage.model;

import io.quarkus.mongodb.panache.common.MongoEntity;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

import java.time.Instant;
import java.util.List;

/**
 * Represents a DNS query log entry stored in MongoDB.
 * <p>
 * Contains information about DNS queries processed by the service, including
 * the queried domain, resolution status, response code, and answers.
 */
@MongoEntity(collection = "dns_logs", database = "dns_service")
public class DNSLogEntry {
    /**
     * MongoDB unique identifier.
     */
    @BsonId
    public ObjectId id;
    /**
     * Domain name that was queried.
     */
    public String domain;
    /**
     * Resolution status (e.g., "RESOLVED", "FILTERED", "FAILED").
     */
    public String status;
    /**
     * DNS response code (RCODE) from the resolution.
     */
    public int rcode;
    /**
     * List of DNS answers returned for the query.
     */
    public List<String> answers;
    /**
     * Timestamp when the query was processed.
     */
    public Instant timestamp;
}
