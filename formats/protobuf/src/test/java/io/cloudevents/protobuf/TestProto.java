package io.cloudevents.protobuf;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.protobuf.Any;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Timestamp;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.v1.proto.CloudEvent;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * Test that we can serialize CloudEvents to the protobuf format and back again.
 */
public class TestProto {

    public io.cloudevents.CloudEvent testEvent = CloudEventBuilder.v1()
        .withId("foo")
        .withSource(URI.create("me"))
        .withType("test")
        .withExtension("urirefext", URI.create("foo/bar"))
        .withExtension("uriext", URI.create("https://www.foo.bar"))
        .withExtension("stringext", "ext")
        .withExtension("intext", 3)
        .withExtension("boolext", true)
        .withExtension("timeext", OffsetDateTime.now(ZoneId.of("UTC")))
        .build();

    ProtobufFormat format = new ProtobufFormat();

    @Test
    public void testBasic() throws InvalidProtocolBufferException {
        byte[] raw = format.serialize(testEvent);
        final CloudEvent proto = CloudEvent.parseFrom(raw);
        assertEquals(testEvent.getId(), proto.getId());
        assertEquals(testEvent.getSource().toString(), proto.getSource());
        assertEquals(testEvent.getType(), proto.getType());
        assertEquals(testEvent.getSpecVersion().toString(), proto.getSpecVersion());

        io.cloudevents.CloudEvent testEvent2 = format.deserialize(proto.toByteArray());
        assertEquals(testEvent, testEvent2);
    }

    @Test
    public void testProtoData() throws InvalidProtocolBufferException {
        Timestamp ts = Timestamp.newBuilder().setSeconds(1).setNanos(1).build();
        io.cloudevents.CloudEvent ce = CloudEventBuilder.from(testEvent)
            .withData(ProtobufFormat.PROTO_DATA_CONTENT_TYPE,  Any.pack(ts).toByteArray())
            .build();

        CloudEvent protoCe = CloudEvent.parseFrom(format.serialize(ce));
        assertTrue(protoCe.hasProtoData());
        Any any = protoCe.getProtoData();
        Timestamp unpacked = any.unpack(Timestamp.class);
        assertEquals(ts, unpacked);

    }

    @Test
    public void testBinaryData() throws InvalidProtocolBufferException {
        final byte[] rawData = {1, 3, 3, 7};
        io.cloudevents.CloudEvent ce = CloudEventBuilder.from(testEvent)
            .withData("application/octet-stream", rawData)
            .build();

        CloudEvent protoCe = CloudEvent.parseFrom(format.serialize(ce));
        assertTrue(protoCe.hasBinaryData());

        assertArrayEquals(rawData, protoCe.getBinaryData().toByteArray());
    }

    @Test
    public void testTextData() throws InvalidProtocolBufferException {
        Map<String, String> mediaTypeToData = new HashMap<>();

        final String json = "{\"foo\": [\"bar\"]}";

        io.cloudevents.CloudEvent ce = CloudEventBuilder.from(testEvent)
            .withData("application/json", json.getBytes(StandardCharsets.UTF_8))
            .build();
        CloudEvent protoCe = CloudEvent.parseFrom(format.serialize(ce));
        assertTrue(protoCe.hasTextData());

        assertEquals(json, protoCe.getTextData());
    }
}
