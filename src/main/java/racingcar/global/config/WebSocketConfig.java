package racingcar.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private static final String WEBSOCKET_PATH = "/car-ws";

    private static final String WEBSOCKET_SUBSCRIPTION_PATH = "/sub";
    private static final String WEBSOCKET_PUBLISH_PATH = "/pub";

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint(WEBSOCKET_PATH)
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry messageBrokerRegistry) {
        messageBrokerRegistry.enableSimpleBroker(WEBSOCKET_SUBSCRIPTION_PATH);
        messageBrokerRegistry.setApplicationDestinationPrefixes(WEBSOCKET_PUBLISH_PATH);
    }
}
