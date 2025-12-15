package dev.pacr.dns.storage.model;

import io.quarkus.mongodb.panache.common.MongoEntity;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

import java.time.Instant;
import java.util.List;

@MongoEntity(collection = "dns_logs", database = "dns_service")
public class DNSLogEntry {
    @BsonId
    public ObjectId id; // MongoDB _id
    public String domain;
    public String status;
    public int rcode;
    public List<String> answers;
    public Instant timestamp;
}
