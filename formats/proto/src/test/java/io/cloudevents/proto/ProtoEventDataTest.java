package io.cloudevents.proto;

import io.cloudevents.CloudEvent;
import io.cloudevents.CloudEventData;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import javax.json.JsonValue;
import java.io.IOException;
import java.util.stream.Stream;

import static io.cloudevents.core.test.Data.V1_WITH_JSON_DATA;
import static io.cloudevents.core.test.Data.V1_WITH_JSON_DATA_WITH_EXT;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test/Verify data handling..
 */
public class ProtoEventDataTest {

    ProtoFormat format = new ProtoFormat();

    @ParameterizedTest
    @MethodSource("jsonTestData")
    public void jsonData(io.cloudevents.CloudEvent input) throws IOException
    {
        // Serialize the event.
        byte[] raw = format.serialize(input);

        // re-Load the protobuf wire payload
        CloudEvent cloudEvent = format.deserialize(raw);

        assertNotNull(cloudEvent);
        CloudEventData ced = cloudEvent.getData();

        // Sanity
        assertNotNull(ced);

        // Expected Type
        assertTrue(ced instanceof JsonValueCloudEventData);

        JsonValueCloudEventData jData = (JsonValueCloudEventData) ced;

        // Text Accessors
        String text = jData.getText();
        assertEquals("{}", text);

        // JSON Accessor
        JsonValue json = jData.getJsonValue();
        assertEquals(json.getValueType(), JsonValue.ValueType.OBJECT);

    }

    public static Stream<Arguments> jsonTestData()
    {
        return Stream.of(

            Arguments.of(V1_WITH_JSON_DATA),
            Arguments.of(V1_WITH_JSON_DATA_WITH_EXT)

        );
    }

}
