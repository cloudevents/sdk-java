package io.cloudevents.examples.spring;

import java.net.URI;
import java.util.UUID;
import java.util.function.Function;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.http.codec.CodecCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.CodecConfigurer;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.spring.messaging.CloudEventMessageConverter;
import io.cloudevents.spring.webflux.CloudEventHttpMessageReader;
import io.cloudevents.spring.webflux.CloudEventHttpMessageWriter;

@SpringBootApplication
public class DemoApplication {
	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Bean
	public Function<CloudEvent, CloudEvent> events() {
		return event -> CloudEventBuilder.from(event)
				.withId(UUID.randomUUID().toString())
				.withSource(URI.create("https://spring.io/foos"))
				.withType("io.spring.event.Foo")
				.withData(event.getData().toBytes())
				.build();
	}

	/**
	 * Configure a MessageConverter for Spring Cloud Function to pick up and use to
	 * convert to and from CloudEvent and Message.
	 */
	@Configuration
	public static class CloudEventMessageConverterConfiguration {
		@Bean
		public CloudEventMessageConverter cloudEventMessageConverter() {
			return new CloudEventMessageConverter();
		}
	}

	/**
	 * Configure an HTTP reader and writer so that we can process CloudEvents over
     * HTTP via Spring Webflux.
	 */
	@Configuration
	public static class CloudEventHandlerConfiguration implements CodecCustomizer {
		@Override
		public void customize(CodecConfigurer configurer) {
			configurer.customCodecs().register(new CloudEventHttpMessageReader());
			configurer.customCodecs().register(new CloudEventHttpMessageWriter());
		}
	}
}
