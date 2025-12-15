package dev.pacr.dns.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import org.jboss.logging.Logger;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket endpoint for real-time dashboard updates
 * <p>
 * Maintains persistent connections to dashboard clients and streams: - Metrics updates (query
 * count, cache hits, filters, threats) - Log entries (query logs, security alerts) - Statistics
 * (cache stats, security stats)
 */
@ServerEndpoint("/ws/dashboard")
@ApplicationScoped
public class DashboardWebSocket {
	
	private static final Logger LOG = Logger.getLogger(DashboardWebSocket.class);
	private static final Map<String, Session> SESSIONS = new ConcurrentHashMap<>();
	private final ObjectMapper objectMapper = new ObjectMapper();
	
	/**
	 * Broadcast a message to all connected clients
	 */
	public static void broadcast(Object message) {
		Set<String> failedSessions = Collections.synchronizedSet(new HashSet<>());
		SESSIONS.forEach((sessionId, session) -> {
			try {
				ObjectMapper mapper = new ObjectMapper();
				String json = mapper.writeValueAsString(message);
				if (session.isOpen()) {
					session.getAsyncRemote().sendText(json);
				} else {
					failedSessions.add(sessionId);
				}
			} catch (Exception e) {
				LOG.warnf("Failed to broadcast to session %s: %s", sessionId, e.getMessage());
				failedSessions.add(sessionId);
			}
		});
		
		// Clean up closed sessions
		failedSessions.forEach(SESSIONS::remove);
	}
	
	/**
	 * Send a message to a specific session
	 */
	private static void sendMessage(Session session, Map<String, Object> message) {
		try {
			if (session.isOpen()) {
				ObjectMapper mapper = new ObjectMapper();
				String json = mapper.writeValueAsString(message);
				session.getAsyncRemote().sendText(json);
			}
		} catch (Exception e) {
			LOG.warnf(e, "Failed to send message to session %s", session.getId());
		}
	}
	
	/**
	 * Get the number of connected clients
	 */
	public static int getConnectedClientCount() {
		return SESSIONS.size();
	}
	
	@OnOpen
	public void onOpen(Session session) {
		String sessionId = session.getId();
		SESSIONS.put(sessionId, session);
		LOG.infof("Dashboard WebSocket client connected: %s (total: %d)", sessionId,
				SESSIONS.size());
		
		// Send welcome message
		sendMessage(session,
				Map.of("type", "CONNECTED", "message", "Connected to dashboard event stream",
						"sessionId", sessionId));
	}
	
	@OnClose
	public void onClose(Session session) {
		String sessionId = session.getId();
		SESSIONS.remove(sessionId);
		LOG.infof("Dashboard WebSocket client disconnected: %s (total: %d)", sessionId,
				SESSIONS.size());
	}
	
	@OnError
	public void onError(Session session, Throwable throwable) {
		String sessionId = session.getId();
		LOG.warnf(throwable, "Dashboard WebSocket error for session %s", sessionId);
		try {
			session.close();
		} catch (IOException e) {
			LOG.warnf("Failed to close WebSocket session after error: %s", sessionId);
		}
		SESSIONS.remove(sessionId);
	}
	
	@OnMessage
	public void onMessage(String message, Session session) {
		try {
			JsonNode json = objectMapper.readTree(message);
			String type = json.get("type").asText();
			
			LOG.debugf("Dashboard WebSocket message received: %s from session %s", type,
					session.getId());
			
			switch (type) {
				case "PING":
					sendMessage(session, Map.of("type", "PONG"));
					break;
				case "SUBSCRIBE":
					handleSubscribe(json, session);
					break;
				case "UNSUBSCRIBE":
					handleUnsubscribe(json, session);
					break;
				default:
					LOG.warnf("Unknown message type: %s", type);
			}
		} catch (Exception e) {
			LOG.warnf(e, "Error processing WebSocket message");
			sendMessage(session, Map.of("type", "ERROR", "message", "Failed to process message"));
		}
	}
	
	/**
	 * Handle subscription requests
	 */
	private void handleSubscribe(JsonNode message, Session session) {
		String channel = message.get("channel").asText();
		LOG.infof("Session %s subscribed to channel: %s", session.getId(), channel);
		sendMessage(session, Map.of("type", "SUBSCRIBED", "channel", channel));
	}
	
	/**
	 * Handle unsubscription requests
	 */
	private void handleUnsubscribe(JsonNode message, Session session) {
		String channel = message.get("channel").asText();
		LOG.infof("Session %s unsubscribed from channel: %s", session.getId(), channel);
		sendMessage(session, Map.of("type", "UNSUBSCRIBED", "channel", channel));
	}
}
