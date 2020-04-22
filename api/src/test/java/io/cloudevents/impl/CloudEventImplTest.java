package io.cloudevents.impl;

import io.cloudevents.CloudEvent;
import org.junit.jupiter.api.Test;

import static io.cloudevents.test.Data.*;
import static org.assertj.core.api.Assertions.assertThat;

public class CloudEventImplTest {

    @Test
    public void testEqualityV03() {
        CloudEvent event1 = CloudEvent.buildV03()
            .withId(ID)
            .withType(TYPE)
            .withSource(SOURCE)
            .withData(DATACONTENTTYPE_JSON, DATASCHEMA, DATA_JSON_SERIALIZED)
            .withSubject(SUBJECT)
            .withTime(TIME)
            .build();

        CloudEvent event2 = CloudEvent.buildV03()
            .withId(ID)
            .withType(TYPE)
            .withSource(SOURCE)
            .withData(DATACONTENTTYPE_JSON, DATASCHEMA, DATA_JSON_SERIALIZED)
            .withSubject(SUBJECT)
            .withTime(TIME)
            .build();

        assertThat(event1).isEqualTo(event2);
    }

    @Test
    public void testEqualityV1() {
        CloudEvent event1 = CloudEvent.buildV1()
            .withId(ID)
            .withType(TYPE)
            .withSource(SOURCE)
            .withData(DATACONTENTTYPE_JSON, DATASCHEMA, DATA_JSON_SERIALIZED)
            .withSubject(SUBJECT)
            .withTime(TIME)
            .build();

        CloudEvent event2 = CloudEvent.buildV1()
            .withId(ID)
            .withType(TYPE)
            .withSource(SOURCE)
            .withData(DATACONTENTTYPE_JSON, DATASCHEMA, DATA_JSON_SERIALIZED)
            .withSubject(SUBJECT)
            .withTime(TIME)
            .build();

        assertThat(event1).isEqualTo(event2);
    }

}
