package io.cloudevents.springboot.autoconfigure.http.codec;

import io.cloudevents.CloudEvent;
import io.cloudevents.spring.webflux.CloudEventHttpMessageReader;
import io.cloudevents.spring.webflux.CloudEventHttpMessageWriter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.http.codec.CodecsAutoConfiguration;
import org.springframework.boot.web.codec.CodecCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Eddú Meléndez
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({CloudEvent.class, CloudEventHttpMessageReader.class})
@AutoConfigureBefore(CodecsAutoConfiguration.class)
@ConditionalOnProperty(name = "cloudevents.spring.webflux.enabled", havingValue = "true", matchIfMissing = true)
public class CloudEventsCodecAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public CodecCustomizer cloudEventsCodecCustomizer() {
        return configurer -> {
            configurer.customCodecs().register(new CloudEventHttpMessageReader());
            configurer.customCodecs().register(new CloudEventHttpMessageWriter());
        };
    }

}
