package io.cloudevents.test;

import io.cloudevents.CloudEvent;
import io.cloudevents.types.Time;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.stream.Stream;

public class Data {

    public static final String ID = "1";
    public static final String TYPE = "mock.test";
    public static final URI SOURCE = URI.create("http://localhost/source");
    public static final String DATACONTENTTYPE_JSON = "application/json";
    public static final String DATACONTENTTYPE_XML = "application/xml";
    public static final String DATACONTENTTYPE_TEXT = "text/plain";
    public static final URI DATASCHEMA = URI.create("http://localhost/schema");
    public static final String SUBJECT = "sub";
    public static final ZonedDateTime TIME = Time.parseTime("2018-04-26T14:48:09+02:00");

    public static byte[] DATA_JSON_SERIALIZED = "{}".getBytes();
    public static byte[] DATA_XML_SERIALIZED = "<stuff></stuff>".getBytes();
    public static byte[] DATA_TEXT_SERIALIZED = "Hello World Lorena!".getBytes();

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

    public static final CloudEvent V1_WITH_JSON_DATA_WITH_EXT = CloudEvent.buildV1()
        .withId(ID)
        .withType(TYPE)
        .withSource(SOURCE)
        .withData(DATACONTENTTYPE_JSON, DATASCHEMA, DATA_JSON_SERIALIZED)
        .withSubject(SUBJECT)
        .withTime(TIME)
        .withExtension("astring", "aaa")
        .withExtension("aboolean", true)
        .withExtension("anumber", 10)
        .build();

    public static final CloudEvent V1_WITH_JSON_DATA_WITH_EXT_STRING = CloudEvent.buildV1()
        .withId(ID)
        .withType(TYPE)
        .withSource(SOURCE)
        .withData(DATACONTENTTYPE_JSON, DATASCHEMA, DATA_JSON_SERIALIZED)
        .withSubject(SUBJECT)
        .withTime(TIME)
        .withExtension("astring", "aaa")
        .withExtension("aboolean", "true")
        .withExtension("anumber", "10")
        .build();

    public static final CloudEvent V1_WITH_XML_DATA = CloudEvent.buildV1()
        .withId(ID)
        .withType(TYPE)
        .withSource(SOURCE)
        .withData(DATACONTENTTYPE_XML, DATA_XML_SERIALIZED)
        .withSubject(SUBJECT)
        .withTime(TIME)
        .build();

    public static final CloudEvent V1_WITH_TEXT_DATA = CloudEvent.buildV1()
        .withId(ID)
        .withType(TYPE)
        .withSource(SOURCE)
        .withData(DATACONTENTTYPE_TEXT, DATA_TEXT_SERIALIZED)
        .withSubject(SUBJECT)
        .withTime(TIME)
        .build();

    public static final CloudEvent V03_MIN = V1_MIN.toV03();
    public static final CloudEvent V03_WITH_JSON_DATA = V1_WITH_JSON_DATA.toV03();
    public static final CloudEvent V03_WITH_JSON_DATA_WITH_EXT = V1_WITH_JSON_DATA_WITH_EXT.toV03();
    public static final CloudEvent V03_WITH_JSON_DATA_WITH_EXT_STRING = V1_WITH_JSON_DATA_WITH_EXT_STRING.toV03();
    public static final CloudEvent V03_WITH_XML_DATA = V1_WITH_XML_DATA.toV03();
    public static final CloudEvent V03_WITH_TEXT_DATA = V1_WITH_TEXT_DATA.toV03();

    public static Stream<CloudEvent> allEvents() {
        return Stream.concat(v1Events(), v03Events());
    }

    public static Stream<CloudEvent> allEventsWithoutExtensions() {
        return Stream.concat(v1Events(), v03Events()).filter(e -> e.getExtensions().isEmpty());
    }

    public static Stream<CloudEvent> allEventsWithStringExtensions() {
        return Stream.concat(v1EventsWithStringExt(), v03EventsWithStringExt());
    }

    public static Stream<CloudEvent> v1Events() {
        return Stream.of(
            Data.V1_MIN,
            Data.V1_WITH_JSON_DATA,
            Data.V1_WITH_JSON_DATA_WITH_EXT,
            Data.V1_WITH_XML_DATA,
            Data.V1_WITH_TEXT_DATA
        );
    }

    public static Stream<CloudEvent> v03Events() {
        return Stream.of(
            Data.V03_MIN,
            Data.V03_WITH_JSON_DATA,
            Data.V03_WITH_JSON_DATA_WITH_EXT,
            Data.V03_WITH_XML_DATA,
            Data.V03_WITH_TEXT_DATA
        );
    }

    public static Stream<CloudEvent> v1EventsWithStringExt() {
        return v1Events().map(ce -> {
            io.cloudevents.v1.CloudEventBuilder builder = CloudEvent.buildV1(ce);
            ce.getExtensions().forEach((k, v) -> builder.withExtension(k, v.toString()));
            return builder.build();
        });
    }

    public static Stream<CloudEvent> v03EventsWithStringExt() {
        return v03Events().map(ce -> {
            io.cloudevents.v03.CloudEventBuilder builder = CloudEvent.buildV03(ce);
            ce.getExtensions().forEach((k, v) -> builder.withExtension(k, v.toString()));
            return builder.build();
        });
    }

}
