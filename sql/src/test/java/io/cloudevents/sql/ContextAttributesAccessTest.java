package io.cloudevents.sql;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.core.test.Data;
import org.junit.jupiter.api.Test;

import static io.cloudevents.sql.asserts.MyAssertions.assertThat;

public class ContextAttributesAccessTest {

    @Test
    void requiredAttributeAvailable() {
        assertThat(Parser.parseDefault("id").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asString()
            .isEqualTo(Data.V1_MIN.getId());
    }

    @Test
    void optionalAttributeAvailable() {
        CloudEvent event = CloudEventBuilder.v1()
            .withId(Data.ID)
            .withSource(Data.SOURCE)
            .withType(Data.TYPE)
            .withSubject(Data.SUBJECT)
            .build();

        assertThat(Parser.parseDefault("subject").evaluate(event))
            .isNotFailed()
            .asString()
            .isEqualTo(Data.SUBJECT);
    }

    @Test
    void optionalAttributeAbsent() {
        CloudEvent event = CloudEventBuilder.v1()
            .withId(Data.ID)
            .withSource(Data.SOURCE)
            .withType(Data.TYPE)
            .build();

        assertThat(Parser.parseDefault("subject").evaluate(event))
            .hasFailure(EvaluationException.Kind.MISSING_ATTRIBUTE)
            .asString()
            .isEmpty();
    }

    @Test
    void extensionAvailable() {
        CloudEvent event = CloudEventBuilder.v1()
            .withId(Data.ID)
            .withSource(Data.SOURCE)
            .withType(Data.TYPE)
            .withExtension("mybool", true)
            .build();

        assertThat(Parser.parseDefault("mybool").evaluate(event))
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

        assertThat(Parser.parseDefault("myext").evaluate(event))
            .hasFailure(EvaluationException.Kind.MISSING_ATTRIBUTE)
            .asString()
            .isEmpty();
    }

}
