package io.cloudevents.examples.spring;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.spring.http.CloudEventHttpUtils;
import io.cloudevents.spring.webflux.CloudEventHttpMessageReader;
import io.cloudevents.spring.webflux.CloudEventHttpMessageWriter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.http.codec.CodecCustomizer;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.CodecConfigurer;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.UUID;

@SpringBootApplication
@RestController
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@PostMapping("/foos")
	// Let Spring do the type conversion of request and response body
	public ResponseEntity<Foo> echo(@RequestBody Foo foo, @RequestHeader HttpHeaders headers) {
		CloudEvent attributes = CloudEventHttpUtils.fromHttp(headers) //
				.withId(UUID.randomUUID().toString()) //
				.withSource(URI.create("https://spring.io/foos")) //
				.withType("io.spring.event.Foo") //
				.build();
		HttpHeaders outgoing = CloudEventHttpUtils.toHttp(attributes);
		return ResponseEntity.ok().headers(outgoing).body(foo);
	}

	@PostMapping("/event")
	// Use CloudEvent API and manual type conversion of request and response body
	public Mono<CloudEvent> event(@RequestBody Mono<CloudEvent> body) {
		return body.map(event -> CloudEventBuilder.from(event) //
				.withId(UUID.randomUUID().toString()) //
				.withSource(URI.create("https://spring.io/foos")) //
				.withType("io.spring.event.Foo") //
				.withData(event.getData().toBytes()) //
				.build());
	}

	@Configuration
	public static class CloudEventHandlerConfiguration implements CodecCustomizer {

		@Override
		public void customize(CodecConfigurer configurer) {
			configurer.customCodecs().register(new CloudEventHttpMessageReader());
			configurer.customCodecs().register(new CloudEventHttpMessageWriter());
		}

	}

    public record Foo(
        String value
    ) {}
}
