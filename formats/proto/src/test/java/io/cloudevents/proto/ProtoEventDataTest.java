package io.cloudevents.proto;

import io.cloudevents.CloudEvent;
import io.cloudevents.CloudEventData;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import javax.json.JsonValue;
import java.io.IOException;
import java.util.stream.Stream;

import static io.cloudevents.core.test.Data.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test/Verify data handling..
 */
public class ProtoEventDataTest {

    ProtoFormat format = new ProtoFormat();

    @ParameterizedTest
    @MethodSource("noTestData")
    public void noData(io.cloudevents.CloudEvent input) throws IOException
    {

        CloudEventData ced = realiseCloudEventData(input);

        assertNull(ced);
    }

    @ParameterizedTest
    @MethodSource("jsonTestData")
    public void jsonData(io.cloudevents.CloudEvent input) throws IOException
    {

        CloudEventData ced = realiseCloudEventData(input);

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

    @ParameterizedTest
    @MethodSource("textTestData")
    public void textData(io.cloudevents.CloudEvent input) throws IOException
    {

        CloudEventData ced = realiseCloudEventData(input);

        // Sanity
        assertNotNull(ced);

        // Expected Type
        assertTrue(ced instanceof TextCloudEventData);

        assertNotNull(((TextCloudEventData) ced).getText());

    }


    public static Stream<Arguments> jsonTestData()
    {
        return Stream.of(

            Arguments.of(V1_WITH_JSON_DATA),
            Arguments.of(V1_WITH_JSON_DATA_WITH_EXT)

        );
    }

    public static Stream<Arguments> textTestData()
    {
        return Stream.of(

            Arguments.of(V1_WITH_XML_DATA),
            Arguments.of(V1_WITH_TEXT_DATA),
            Arguments.of(V1_WITH_JSON_DATA)

        );
    }

    public static Stream<Arguments> noTestData()
    {
        return Stream.of(

            Arguments.of(V1_MIN)

        );
    }

    private  CloudEventData realiseCloudEventData(CloudEvent srcEvent) {

        // Serialize the event.
        byte[] raw = format.serialize(srcEvent);

        // re-Load the protobuf wire payload
        CloudEvent cloudEvent = format.deserialize(raw);

        // Make sure we actually managed to construct something.
        assertNotNull(cloudEvent);

        // And give baack the data
        return cloudEvent.getData();

    }

}
