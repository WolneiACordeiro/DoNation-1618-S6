package com.fatec.donation.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * Configura o broker de mensagens (STOMP).
     * Define os prefixos de destino e o broker para onde as mensagens serão enviadas.
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Configura um broker simples baseado na memória para os destinos "/topic" e "/queue"
        registry.enableSimpleBroker("/topic", "/queue");

        // Define o prefixo usado para rotas do lado do servidor
        registry.setApplicationDestinationPrefixes("/app");
    }

    /**
     * Registra os endpoints WebSocket e habilita fallback com SockJS.
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Registra o endpoint do WebSocket com suporte ao fallback SockJS
        registry.addEndpoint("/chat-websocket")
                .setAllowedOrigins("*") // Permite conexões de qualquer origem (configuração CORS)
                .withSockJS();
    }
}
