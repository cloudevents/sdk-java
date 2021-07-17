package io.cloudevents.examples.spring;

import java.net.URI;
import java.util.UUID;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import reactor.core.publisher.Mono;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

@SpringBootApplication
@Controller
public class DemoApplication {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(DemoApplication.class, args);
	}

	@MessageMapping("event")
	// Use CloudEvent API and manual type conversion of request and response body
	public Mono<CloudEvent> event(@RequestBody Mono<CloudEvent> body) {
		return body.map(event -> CloudEventBuilder.from(event) //
				.withId(UUID.randomUUID().toString()) //
				.withSource(URI.create("https://spring.io/foos")) //
				.withType("io.spring.event.Foo") //
				.withData(event.getData().toBytes()) //
				.build());
	}

}
