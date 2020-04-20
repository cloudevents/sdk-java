package io.cloudevents.test;

import io.cloudevents.CloudEvent;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.UUID;
import java.util.stream.Stream;

public class Data {

    public static final String ID = UUID.randomUUID().toString();
    public static final String TYPE = "mock.test";
    public static final URI SOURCE = URI.create("http://localhost/source");
    public static final String DATACONTENTTYPE_JSON = "application/json";
    public static final URI DATASCHEMA = URI.create("http://localhost/schema");
    public static final String SUBJECT = "sub";
    public static final ZonedDateTime TIME = ZonedDateTime.now();

    public static byte[] DATA_JSON_SERIALIZED = "{}".getBytes();

    public static final CloudEvent V1_MIN = CloudEvent.buildV1()
        .withId(ID)
        .withType(TYPE)
        .withSource(SOURCE)
        .build();

    public static final CloudEvent V1_WITH_JSON_DATA = CloudEvent.buildV1()
        .withId(ID)
        .withType(TYPE)
        .withSource(SOURCE)
        .withData(DATACONTENTTYPE_JSON, DATASCHEMA, DATA_JSON_SERIALIZED)
        .withSubject(SUBJECT)
        .withTime(TIME)
        .build();

    public static Stream<CloudEvent> allEvents() {
        return Stream.concat(v1Events(), v03Events());
    }

    public static Stream<CloudEvent> v1Events() {
        return Stream.of(
            Data.V1_MIN,
            Data.V1_WITH_JSON_DATA
        );
    }

    public static Stream<CloudEvent> v03Events() {
        return v1Events().map(CloudEvent::toV03);
    }

}
