package io.cloudevents.sql;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.core.test.Data;
import org.junit.jupiter.api.Test;

import java.util.Base64;

import static io.cloudevents.sql.asserts.MyAssertions.assertThat;

public class ContextAttributesAccessTest {

    @Test
    void stringConversionForComplexTypes() {
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
