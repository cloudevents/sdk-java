package io.cloudevents.examples.quarkus.client;

import java.net.URI;
import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.cloudevents.CloudEvent;
import io.cloudevents.CloudEventData;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.core.data.PojoCloudEventData;
import io.cloudevents.examples.quarkus.model.User;
import io.quarkus.scheduler.Scheduled;

@ApplicationScoped
public class UserEventsGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserEventsGenerator.class);

    @Inject
    ObjectMapper mapper;

    @Inject
    @RestClient
    UserClient userClient;

    long userCount=0;

    @Scheduled(every="2s")
    public void init() {
        CloudEvent event = createEvent(userCount++);
        if(userCount % 2 == 0) {
            LOGGER.info("try to emit binary event for user: {}", event.getId());
            userClient.emitBinary(event);
        } else {
            LOGGER.info("try to emit structured event for user: {}", event.getId());
            userClient.emitStructured(event);
        }
    }

    private CloudEvent createEvent(long id) {
        return CloudEventBuilder.v1()
            .withSource(URI.create("example"))
            .withType("io.cloudevents.examples.quarkus.user")
            .withId(UUID.randomUUID().toString())
            .withDataContentType(MediaType.APPLICATION_JSON)
            .withData(createUser(id))
            .build();
    }

    private CloudEventData createUser(Long id) {
        User user = new User()
            .setAge(id.intValue())
            .setUsername("user" + id)
            .setFirstName("firstName" + id)
            .setLastName("lastName" + id);
        return PojoCloudEventData.wrap(user, mapper::writeValueAsBytes);
    }
}
