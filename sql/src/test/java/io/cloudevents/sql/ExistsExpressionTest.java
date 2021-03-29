package io.cloudevents.sql;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.core.test.Data;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static io.cloudevents.sql.asserts.MyAssertions.assertThat;

public class ExistsExpressionTest {

    @Test
    void requiredAttributesAlwaysTrue() {
        assertThat(Parser.parseDefault("EXISTS id").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asBoolean()
            .isTrue();
    }

    @Test
    void optionalAttributeAvailable() {
        CloudEvent event = CloudEventBuilder.v1()
            .withId(Data.ID)
            .withSource(Data.SOURCE)
            .withType(Data.TYPE)
            .withTime(OffsetDateTime.now())
            .build();

        assertThat(Parser.parseDefault("EXISTS time").evaluate(event))
            .isNotFailed()
            .asBoolean()
            .isTrue();
    }

    @Test
    void optionalAttributeAbsent() {
        CloudEvent event = CloudEventBuilder.v1()
            .withId(Data.ID)
            .withSource(Data.SOURCE)
            .withType(Data.TYPE)
            .build();

        assertThat(Parser.parseDefault("EXISTS time").evaluate(event))
            .isNotFailed()
            .asBoolean()
            .isFalse();
    }

    @Test
    void extensionAvailable() {
        CloudEvent event = CloudEventBuilder.v1()
            .withId(Data.ID)
            .withSource(Data.SOURCE)
            .withType(Data.TYPE)
            .withTime(OffsetDateTime.now())
            .withExtension("myext", 123)
            .build();

        assertThat(Parser.parseDefault("EXISTS myext").evaluate(event))
            .isNotFailed()
            .asBoolean()
            .isTrue();
    }

    @Test
    void extensionAbsent() {
        CloudEvent event = CloudEventBuilder.v1()
            .withId(Data.ID)
            .withSource(Data.SOURCE)
            .withType(Data.TYPE)
            .build();

        assertThat(Parser.parseDefault("EXISTS myext").evaluate(event))
            .isNotFailed()
            .asBoolean()
            .isFalse();
    }

}
