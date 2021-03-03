package io.cloudevents.proto;

import io.cloudevents.CloudEvent;
import io.cloudevents.SpecVersion;
import io.cloudevents.rw.CloudEventWriter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.net.URI;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ProtoEventWriterTest {

    CloudEventWriter<CloudEvent> writer = new ProtoEventWriter();

    @BeforeEach
    void setUp()
    {
        writer = new ProtoEventWriter();
    }

    @ParameterizedTest
    @MethodSource("basicWriteAndReadMandatory")
    void basicWriteAndRead(String specVersion, String id, String type, String source)
    {

        writer.withContextAttribute("specversion", specVersion);
        writer.withContextAttribute("id", id);
        writer.withContextAttribute("type", type);
        writer.withContextAttribute("source", source);

        CloudEvent ce = writer.end();

        URI expectedSource = URI.create(source);

        assertNotNull(ce);
        assertEquals(SpecVersion.parse(specVersion), ce.getSpecVersion());
        assertEquals(id, ce.getId());
        assertEquals(type, ce.getType());
        assertEquals(expectedSource, ce.getSource());

    }

    @ParameterizedTest
    @MethodSource("basicWriteAndReadOptionalV1")
    void basicWriteAndReadOptional(String id, String type, String source, String subject, String time, String datacontenttype, String dataschema)
    {

        writer.withContextAttribute("specversion", "1.0");
        writer.withContextAttribute("id", id);
        writer.withContextAttribute("type", type);
        writer.withContextAttribute("source", source);

        writer.withContextAttribute("time", time);
        writer.withContextAttribute("subject", subject);
        writer.withContextAttribute("datacontenttype", datacontenttype);
        writer.withContextAttribute("dataschema", dataschema);

        CloudEvent ce = writer.end();

        URI expectedSource = URI.create(source);
        OffsetDateTime expectedTime = (time != null) ? OffsetDateTime.parse(time) : null;
        URI expectedDataSchema = (dataschema != null) ? URI.create(dataschema) : null;

        assertNotNull(ce);
        assertEquals(id, ce.getId());
        assertEquals(type, ce.getType());
        assertEquals(expectedSource, ce.getSource());
        assertEquals(subject, ce.getSubject());
        assertEquals(datacontenttype, ce.getDataContentType());
        assertEquals(expectedDataSchema, ce.getDataSchema());
        verifyTime(expectedTime, ce.getTime());


    }

    private void verifyTime(OffsetDateTime exp, OffsetDateTime act)
    {

        if (exp == null && act == null) {
            return;
        }
        if (exp == null || act == null) {
            assertEquals(exp, act);
        }

        // Do a timstamp comparison

        Instant expInstant = exp.toInstant();
        Instant actInstant = act.toInstant();

        assertEquals(expInstant, actInstant);

    }


    //-- Data Sets

    public static Stream<Arguments> basicWriteAndReadMandatory()
    {
        return Stream.of(
            Arguments.of("1.0", "ID1", "ORG.TEST.TYPE", "mysource"),
            Arguments.of("0.3", "ID3", "ORG.TEST.TYPE", "mysource")
        );
    }

    public static Stream<Arguments> basicWriteAndReadOptionalV1()
    {
        return Stream.of(
            Arguments.of("ID1", "ORG.TEST.TYPE", "mysource", null, null, null, null),
            Arguments.of("ID2", "ORG.TEST.TYPE", "mysource", "mysubject", null, null, null),
            Arguments.of("ID3", "ORG.TEST.TEST", "asource", null, "2020-12-23T09:49:00-08:00", null, null),
            Arguments.of("ID4", "ORG.TEST.TEST", "asource", null, "2020-12-23T18:25Z", "application/json", null),
            Arguments.of("ID4", "ORG.TEST.TEST", "asource", "secret-subject", "2020-12-23T18:27Z", "application/xml", "http://somecompany.org/aref")
        );
    }

}
