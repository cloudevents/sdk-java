package io.cloudevents.examples.spring;

import java.net.URI;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.core.data.PojoCloudEventData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.util.MimeType;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class DemoApplicationTests {

	@Autowired
	private RSocketRequester.Builder builder;

	@Autowired
	private ObjectMapper mapper;

	private RSocketRequester rsocketRequester;

	@BeforeEach
	public void init() {
		String host = "localhost";
		int port = 7000;
		rsocketRequester = builder
				.dataMimeType(MimeType.valueOf("application/cloudevents+json"))
				.tcp(host, port);
	}

	@Test
	void echoWithCorrectHeaders() {
		CloudEvent result = rsocketRequester.route("event")
				.data(CloudEventBuilder.v1()
						.withDataContentType("application/cloudevents+json")
						.withId(UUID.randomUUID().toString()) //
						.withSource(URI.create("https://spring.io/foos")) //
						.withType("io.spring.event.Foo") //
						.withData(PojoCloudEventData.wrap(new Foo("Dave"),
								foo -> mapper.writeValueAsBytes(foo)))
						.build())
				.retrieveMono(CloudEvent.class).block();

		assertThat(new String(result.getData().toBytes()))
				.isEqualTo("{\"value\":\"Dave\"}");

	}

}
