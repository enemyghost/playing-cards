package com.gmo.big2.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * @author tedelen
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/v1/games/push/{gameId}")
                .setAllowedOrigins("http://c6db7663.ngrok.io", "https://whispering-ocean-60773.herokuapp.com", "http://localhost:3000");
        registry.addEndpoint("/v1/games/push/{gameId}")
                .setAllowedOrigins("http://c6db7663.ngrok.io", "https://whispering-ocean-60773.herokuapp.com", "http://localhost:3000")
                .withSockJS();
    }
}
