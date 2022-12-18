package io.cloudevents.springboot.autoconfigure.rsocket;

import io.cloudevents.CloudEvent;
import io.cloudevents.spring.codec.CloudEventDecoder;
import io.cloudevents.spring.codec.CloudEventEncoder;
import io.rsocket.RSocket;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.rsocket.RSocketStrategiesAutoConfiguration;
import org.springframework.boot.rsocket.messaging.RSocketStrategiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.rsocket.RSocketStrategies;

/**
 * @author Eddú Meléndez
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({CloudEvent.class, CloudEventEncoder.class, RSocket.class, RSocketStrategies.class, RSocketStrategiesCustomizer.class})
@AutoConfigureBefore(RSocketStrategiesAutoConfiguration.class)
@ConditionalOnProperty(name = "cloudevents.spring.rsocket.enabled", havingValue = "true", matchIfMissing = true)
public class CloudEventsStrategyAutoConfiguration {

    @Bean
    @Order(-1)
    @ConditionalOnMissingBean
    public RSocketStrategiesCustomizer cloudEventsRSocketStrategiesCustomizer() {
        return strategies -> {
            strategies.encoder(new CloudEventEncoder());
            strategies.decoder(new CloudEventDecoder());
        };
    }

}
