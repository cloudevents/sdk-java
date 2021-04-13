package io.cloudevents.sql;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.core.test.Data;
import org.junit.jupiter.api.Test;

import java.util.Base64;

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
            .hasFailure(EvaluationException.ErrorKind.MISSING_ATTRIBUTE)
            .asString()
            .isEmpty();
    }

    @Test
    void booleanExtensionAvailable() {
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
    void integerExtensionAvailable() {
        CloudEvent event = CloudEventBuilder.v1()
            .withId(Data.ID)
            .withSource(Data.SOURCE)
            .withType(Data.TYPE)
            .withExtension("numberext", 10)
            .build();

        assertThat(Parser.parseDefault("numberext").evaluate(event))
            .isNotFailed()
            .asInteger()
            .isEqualTo(event.getExtension("numberext"));
    }

    @Test
    void extensionAbsent() {
        CloudEvent event = CloudEventBuilder.v1()
            .withId(Data.ID)
            .withSource(Data.SOURCE)
            .withType(Data.TYPE)
            .build();

        assertThat(Parser.parseDefault("myext").evaluate(event))
            .hasFailure(EvaluationException.ErrorKind.MISSING_ATTRIBUTE)
            .asString()
            .isEmpty();
    }


    // TODO Don't remove this one!!!
    @Test
    void typeCohercion() {
        CloudEvent event = CloudEventBuilder.v1()
            .withId(Data.ID)
            .withType(Data.TYPE)
            .withSource(Data.SOURCE)
            .withData(Data.DATACONTENTTYPE_JSON, Data.DATASCHEMA, Data.DATA_JSON_SERIALIZED)
            .withSubject(Data.SUBJECT)
            .withTime(Data.TIME)
            .withExtension("numberext", 10)
            .withExtension("binaryext", new byte[]{0x01, 0x02, 0x03})
            .build();

        assertThat(Parser.parseDefault("source").evaluate(event))
            .isNotFailed()
            .asString()
            .isEqualTo(event.getSource().toString());
        assertThat(Parser.parseDefault("time").evaluate(event))
            .isNotFailed()
            .asString()
            .isEqualTo(event.getTime().toString());
        assertThat(Parser.parseDefault("dataschema").evaluate(event))
            .isNotFailed()
            .asString()
            .isEqualTo(event.getDataSchema().toString());
        assertThat(Parser.parseDefault("binaryext").evaluate(event))
            .isNotFailed()
            .asString()
            .isEqualTo(Base64.getEncoder().encodeToString((byte[]) event.getExtension("binaryext")));
    }
}
