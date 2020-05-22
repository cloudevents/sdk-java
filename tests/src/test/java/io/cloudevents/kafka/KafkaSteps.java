package io.cloudevents.kafka;

import io.cloudevents.CloudEventsSteps;
import io.cucumber.datatable.DataTable;
import io.cucumber.guice.ScenarioScoped;
import io.cucumber.java8.En;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.header.internals.RecordHeaders;

import javax.inject.Inject;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;

@ScenarioScoped
public class KafkaSteps implements En {

    private byte[] payload;

    private Headers headers;

    @Inject
    public KafkaSteps(CloudEventsSteps cloudEventsSteps) {
        Given("Kafka Protocol Binding is supported", () -> {
        });

        Given("a Kafka message with payload:", (String payload) -> {
            this.payload = payload.getBytes(StandardCharsets.UTF_8);
        });

        Given("Kafka headers:", (DataTable dataTable) -> {
            Map<String, String> headers = dataTable.asMap(String.class, String.class);

            this.headers = new RecordHeaders();
            headers.forEach((key, value) -> {
                this.headers.add(key, value.getBytes(StandardCharsets.UTF_8));
            });
        });

        When("parsed as Kafka message", () -> {
            CloudEventDeserializer deserializer = new CloudEventDeserializer();
            cloudEventsSteps.cloudEvent = Optional.of(
                deserializer.deserialize("test", headers, payload)
            );
        });
    }
}
