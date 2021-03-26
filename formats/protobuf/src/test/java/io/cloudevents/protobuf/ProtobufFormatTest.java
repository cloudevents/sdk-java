package io.cloudevents.protobuf;

import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;
import io.cloudevents.core.format.EventFormat;
import io.cloudevents.core.provider.EventFormatProvider;
import io.cloudevents.v1.proto.CloudEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.*;
import java.net.URL;
import java.util.stream.Stream;

import static com.google.common.truth.extensions.proto.ProtoTruth.assertThat;
import static io.cloudevents.core.test.Data.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProtobufFormatTest {

    EventFormat format = new ProtobufFormat();

    @Test
    public void testRegistration() {
        EventFormat act = EventFormatProvider.getInstance().resolveFormat("application/cloudevents+protobuf");

        assertNotNull(act);
        assertTrue(act instanceof ProtobufFormat);
    }

    @ParameterizedTest
    @MethodSource("serializeTestArgumentsDefault")
    public void serialize(io.cloudevents.CloudEvent input, String jsonFile) throws IOException {
        // Serialize the event.
        byte[] raw = format.serialize(input);

        System.out.println("Serialized Size : " + raw.length + " bytes");

        // re-Load the protobuf wire payload
        CloudEvent actualProto = CloudEvent.parseFrom(raw);

        // Load the expected representation
        Message expectedProto = loadProto(jsonFile);

        // Compare.
        assertThat(actualProto).ignoringRepeatedFieldOrder().isEqualTo(expectedProto);

    }

    /**
     * RoundTrip Test
     * <p>
     * Steps:
     * (1) Load the raw proto representation.
     * (2) Deserialize into a CE using the ProtoFormat
     * (3) Serialize the CE into a buffer using the ProtoFormat
     * (4) re-hydrate the protobuf that was serialized in step (3)
     * (5) Ensure the proto from (1) equals the proto from (4)
     *
     * @param filename
     * @throws IOException
     */
    @ParameterizedTest
    @MethodSource("roundTripTestArguments")
    public void roundTripTest(String filename) throws IOException {

        // Load the source (expected) raw proto wire represention.
        byte[] rawData = getProtoData(filename);

        // Create the CloudEvent
        io.cloudevents.CloudEvent expEvent = format.deserialize(rawData);

        // Sanity
        assertNotNull(expEvent);

        // Serialise it back out.
        byte[] raw = format.serialize(expEvent);

        // Sanity
        assertNotNull(raw);
        assertTrue(raw.length > 0);

        // Now read it back
        CloudEvent newProto = CloudEvent.parseFrom(raw);

        // Now hopefully these will match

        CloudEvent expectedProto = CloudEvent.parseFrom(rawData);
        assertThat(newProto).ignoringRepeatedFieldOrder().isEqualTo(expectedProto);

    }

    public static Stream<Arguments> serializeTestArgumentsDefault() {
        return Stream.of(
            Arguments.of(V1_MIN, "v1/min.proto.json"),
            Arguments.of(V1_WITH_JSON_DATA, "v1/json_data.proto.json"),
            Arguments.of(V1_WITH_TEXT_DATA, "v1/text_data.proto.json"),
            Arguments.of(V1_WITH_JSON_DATA_WITH_EXT, "v1/json_data_with_ext.proto.json"),
            Arguments.of(V1_WITH_XML_DATA, "v1/xml_data.proto.json"),
            Arguments.of(V1_WITH_BINARY_EXT, "v1/binary_ext.proto.json"),

            Arguments.of(V03_MIN, "v03/min.proto.json")

        );
    }

    public static Stream<String> roundTripTestArguments() {
        return Stream.of(
            "v1/min.proto.json",
            "v1/json_data.proto.json",
            "v1/text_data.proto.json",
            "v1/json_data_with_ext.proto.json",
            "v1/xml_data.proto.json",
            "v1/binary_ext.proto.json",

            "v03/min.proto.json"
        );
    }

    // ----------------------------------------------------------------

    private static Message loadProto(String filename) throws IOException {

        CloudEvent.Builder b = CloudEvent.newBuilder();
        JsonFormat.parser().ignoringUnknownFields().merge(getReader(filename), b);
        return b.build();
    }

    private static Reader getReader(String filename) throws IOException {

        URL file = Thread.currentThread().getContextClassLoader().getResource(filename);
        File dataFile = new File(file.getFile());
        return new FileReader(dataFile);
    }

    private InputStream getInputStream(String filename) throws IOException {

        URL file = Thread.currentThread().getContextClassLoader().getResource(filename);
        File dataFile = new File(file.getFile());
        return new FileInputStream(dataFile);
    }

    private byte[] getProtoData(String filename) throws IOException {

        Message m = loadProto(filename);
        return m.toByteArray();
    }
}
