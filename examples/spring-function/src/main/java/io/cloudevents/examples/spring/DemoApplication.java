package io.cloudevents.examples.spring;

import java.net.URI;
import java.util.UUID;
import java.util.function.Function;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) throws Exception {
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

}
