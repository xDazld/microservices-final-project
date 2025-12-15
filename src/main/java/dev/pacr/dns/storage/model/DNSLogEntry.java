package dev.pacr.dns.storage.model;

import io.quarkus.mongodb.panache.common.MongoEntity;

import java.time.Instant;
import java.util.List;

@MongoEntity(collection = "dns_logs", database = "dns_service")
public class DNSLogEntry {
	public String id; // MongoDB _id will be mapped automatically when using Panache
	public String domain;
	public String status;
	public int rcode;
	public List<String> answers;
	public Instant timestamp;
}
