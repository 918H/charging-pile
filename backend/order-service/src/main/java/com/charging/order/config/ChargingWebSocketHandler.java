package com.charging.order.config;

import com.charging.order.service.ChargingProgressService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChargingWebSocketHandler extends TextWebSocketHandler {

    private static final Map<String, WebSocketSession> SESSION_MAP = new ConcurrentHashMap<>();
    private static final Map<Long, String> ORDER_SESSION_MAP = new ConcurrentHashMap<>();

    @Resource
    private ChargingProgressService chargingProgressService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String path = session.getUri().getPath();
        String[] parts = path.split("/");
        if (parts.length > 3) {
            String orderId = parts[3];
            SESSION_MAP.put(session.getId(), session);
            ORDER_SESSION_MAP.put(Long.valueOf(orderId), session.getId());
            System.out.println("WebSocket connected for order: " + orderId);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        System.out.println("Received message: " + message.getPayload());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        SESSION_MAP.remove(session.getId());
        for (Map.Entry<Long, String> entry : ORDER_SESSION_MAP.entrySet()) {
            if (entry.getValue().equals(session.getId())) {
                ORDER_SESSION_MAP.remove(entry.getKey());
                break;
            }
        }
        System.out.println("WebSocket disconnected: " + session.getId());
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        System.err.println("WebSocket error: " + exception.getMessage());
    }

    public void sendProgressUpdate(Long orderId, String message) {
        String sessionId = ORDER_SESSION_MAP.get(orderId);
        if (sessionId != null) {
            WebSocketSession session = SESSION_MAP.get(sessionId);
            if (session != null && session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(message));
                } catch (Exception e) {
                    System.err.println("Failed to send message: " + e.getMessage());
                }
            }
        }
    }

    public boolean hasSubscribers(Long orderId) {
        return ORDER_SESSION_MAP.containsKey(orderId);
    }
}
