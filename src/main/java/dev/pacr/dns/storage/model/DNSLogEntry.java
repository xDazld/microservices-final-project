package dev.pacr.dns.storage.model;

import io.quarkus.mongodb.panache.common.MongoEntity;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

import java.time.Instant;
import java.util.List;

@MongoEntity(collection = "dns_logs", database = "dns_service")
/**
	 * DNSLogEntry class.
 */
public class DNSLogEntry {
    @BsonId
    /**
	 * The id.
     */
    public ObjectId id; // MongoDB _id
    /**
	 * The domain.
     */
    public String domain;
    /**
	 * The status.
     */
    public String status;
    /**
	 * The rcode.
     */
    public int rcode;
    /**
	 * The answers.
     */
    public List<String> answers;
    /**
	 * The timestamp.
     */
    public Instant timestamp;
}
