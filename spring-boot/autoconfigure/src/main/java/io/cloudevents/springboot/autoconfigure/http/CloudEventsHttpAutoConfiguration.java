package io.cloudevents.springboot.autoconfigure.http;

import io.cloudevents.CloudEvent;
import io.cloudevents.spring.messaging.CloudEventMessageConverter;
import io.cloudevents.spring.mvc.CloudEventHttpMessageConverter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MessageConverter;

/**
 * @author Eddú Meléndez
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({CloudEvent.class, CloudEventHttpMessageConverter.class, MessageConverter.class})
@AutoConfigureBefore(WebMvcAutoConfiguration.class)
@ConditionalOnProperty(name = "cloudevents.spring.web.enabled", havingValue = "true", matchIfMissing = true)
public class CloudEventsHttpAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public CloudEventMessageConverter cloudEventMessageConverter() {
        return new CloudEventMessageConverter();
    }

}
