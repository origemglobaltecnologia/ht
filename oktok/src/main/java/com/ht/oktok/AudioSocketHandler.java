package com.ht.oktok; // <-- PACOTE CORRIGIDO

import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;
import org.springframework.stereotype.Component;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * AudioSocketHandler lida com mensagens WebSocket binárias (áudio) e de texto (sinais).
 * Ele recebe o áudio/sinal de um cliente e o transmite para todos os outros
 * clientes conectados (funcionalidade de broadcast).
 */
@Component
public class AudioSocketHandler extends BinaryWebSocketHandler {

    private final CopyOnWriteArraySet<WebSocketSession> sessions = new CopyOnWriteArraySet<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);
        System.out.println("Nova conexão WebSocket estabelecida: " + session.getId());
    }

    /**
     * Processa as mensagens binárias (chunks de áudio) e faz broadcast.
     */
    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
        // Itera sobre todas as sessões para broadcast
        broadcast(session, message);
    }

    /**
     * Processa mensagens de texto, como o sinal de parada.
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        // Faz broadcast de mensagens de texto (ex: "STREAM_STOPPED")
        broadcast(session, message);
    }

    /**
     * Lógica de broadcast unificada.
     */
    private void broadcast(WebSocketSession originSession, org.springframework.web.socket.WebSocketMessage<?> message) {
        for (WebSocketSession s : sessions) {
            // Garante que a sessão está aberta e não é a sessão de origem (evita eco)
            if (s.isOpen() && !s.getId().equals(originSession.getId())) {
                try {
                    s.sendMessage(message);
                } catch (Exception e) {
                    System.err.println("Erro ao enviar mensagem para a sessão " + s.getId() + ": " + e.getMessage());
                }
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status) {
        sessions.remove(session);
        System.out.println("Conexão WebSocket encerrada: " + session.getId() + " - Status: " + status.getCode());
    }
}

