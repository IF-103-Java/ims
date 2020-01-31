package com.ita.if103java.ims.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@PropertySource("classpath:application.properties")
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Value("${websocket.endpoint}")
    private String websocketEndpoint;

    @Value("${websocket.allowedOrigins}")
    private String allowedOrigins;

    @Value("${websocket.simplebroker.endpoint}")
    private String simpleBrokerEndpoint;


    @Override
    public void registerStompEndpoints(StompEndpointRegistry stompEndpointRegistry) {
        System.out.println(websocketEndpoint);
        stompEndpointRegistry.addEndpoint(websocketEndpoint).setAllowedOrigins(allowedOrigins).withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker(simpleBrokerEndpoint);
    }

}
