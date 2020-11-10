package io.cloudevents.jackson;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.core.test.Data;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PojoCloudEventDataMapperTest {

    private final JsonNode myPojoJson = JsonNodeFactory.instance.objectNode().put("a", 10).put("b", "Hello World!");
    private final String myPojoSerialized = myPojoJson.toString();
    private final MyPojo myPojo = new MyPojo(10, "Hello World!");

    @Test
    public void testWithBytes() {
        ObjectMapper objectMapper = new ObjectMapper();

        CloudEvent event = CloudEventBuilder.v1(Data.V1_MIN)
            .withData("application/json", myPojoSerialized.getBytes())
            .build();

        PojoCloudEventData<MyPojo> mappedData = event.toData(PojoCloudEventDataMapper.from(objectMapper, new TypeReference<MyPojo>() {
        }));
        assertThat(mappedData)
            .isNotNull()
            .extracting(PojoCloudEventData::getValue)
            .isEqualTo(myPojo);
    }

    @Test
    public void testWithJson() {
        ObjectMapper objectMapper = new ObjectMapper();

        CloudEvent event = CloudEventBuilder.v1(Data.V1_MIN)
            .withData("application/json", new JsonCloudEventData(myPojoJson))
            .build();

        PojoCloudEventData<MyPojo> mappedData = event.toData(PojoCloudEventDataMapper.from(objectMapper, new TypeReference<MyPojo>() {
        }));
        assertThat(mappedData)
            .isNotNull()
            .extracting(PojoCloudEventData::getValue)
            .isEqualTo(myPojo);
    }

}
