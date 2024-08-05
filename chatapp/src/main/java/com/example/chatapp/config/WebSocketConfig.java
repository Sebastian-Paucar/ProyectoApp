package com.example.chatapp.config;

import com.example.chatapp.component.WebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final WebSocketHandler webSocketHandler;

    public WebSocketConfig(WebSocketHandler webSocketHandler) {
        this.webSocketHandler = webSocketHandler;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Configurar el broker de mensajes
        config.enableSimpleBroker("/topic", "/queue", "/session");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Registrar los endpoints de STOMP y permitir el uso de SockJS como fallback
        registry.addEndpoint("/chat-socket")
                .setAllowedOrigins("http://localhost:4200")
                .withSockJS();
    }
}
