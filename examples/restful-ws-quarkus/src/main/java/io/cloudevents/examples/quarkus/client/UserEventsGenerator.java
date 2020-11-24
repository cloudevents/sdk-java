package io.cloudevents.examples.quarkus.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cloudevents.CloudEvent;
import io.cloudevents.CloudEventData;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.core.data.PojoCloudEventData;
import io.cloudevents.examples.quarkus.model.User;
import io.quarkus.runtime.StartupEvent;
import io.smallrye.mutiny.Multi;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import java.net.URI;
import java.time.Duration;
import java.util.UUID;

@ApplicationScoped
public class UserEventsGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserEventsGenerator.class);

    @Inject
    ObjectMapper mapper;

    @Inject
    @RestClient
    UserClient userClient;

    public void init(@Observes StartupEvent startupEvent) {
        Multi.createFrom().ticks().every(Duration.ofSeconds(2))
            .onItem()
            .transform(this::createEvent)
            .subscribe()
            .with(event -> {
                LOGGER.info("try to emit user: {}", event.getId());
                userClient.emit(event);
            });
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
