package com.ht.oktok; // <-- PACOTE CORRIGIDO

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * Configuração do Spring para habilitar e registrar o WebSocket Handler.
 */
@Configuration
@EnableWebSocket // Habilita o suporte a WebSockets no Spring Boot
public class WebSocketConfig implements WebSocketConfigurer {

    private final AudioSocketHandler audioSocketHandler;

    // Injeta o handler criado (Spring agora encontra a classe AudioSocketHandler no mesmo pacote)
    public WebSocketConfig(AudioSocketHandler audioSocketHandler) {
        this.audioSocketHandler = audioSocketHandler;
    }

    /**
     * Registra o handler de áudio no endpoint "/audio".
     */
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(audioSocketHandler, "/audio") // Mapeia o handler para a URL ws://.../audio
                .setAllowedOrigins("*"); // Permite conexões de qualquer origem (CORS - ideal para testes)
    }
}

