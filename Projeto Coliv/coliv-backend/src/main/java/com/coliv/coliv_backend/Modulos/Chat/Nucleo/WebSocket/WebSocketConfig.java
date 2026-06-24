package com.coliv.coliv_backend.Modulos.Chat.Nucleo.WebSocket;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Value("${stomp.relay.host}")
    private String relayHost;
    @Value("${stomp.relay.port}")
    private int relayPort;
    @Value("${stomp.client.login}")
    private String clientLogin;
    @Value("${stomp.client.passcode}")
    private String clientPassCode;
    @Value("${stomp.system.login}")
    private String systemLogin;
    @Value("${stomp.system.passcode}")
    private String systemPassCode;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableStompBrokerRelay("/topic").
                setRelayHost(relayHost).
                setRelayPort(relayPort).
                setClientLogin(clientLogin).
                setClientPasscode(clientPassCode).
                setSystemLogin(systemLogin).
                setSystemPasscode(systemPassCode).
                setSystemHeartbeatSendInterval(25000).
                setSystemHeartbeatReceiveInterval(25000);

        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("ws-connect").setAllowedOriginPatterns("*").withSockJS();
    }
}