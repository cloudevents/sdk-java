package io.cloudevents.jackson;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.core.impl.CloudEventUtils;
import io.cloudevents.core.test.Data;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class PojoCloudEventDataMapperTest {

    private final JsonNode myPojoJson = JsonNodeFactory.instance.objectNode().put("a", 10).put("b", "Hello World!");
    private final String myPojoSerialized = myPojoJson.toString();
    private final MyPojo myPojo = new MyPojo(10, "Hello World!");

    @ParameterizedTest
    @MethodSource("getPojoMappers")
    public void testWithBytes(PojoCloudEventDataMapper<MyPojo> mapper) {

        CloudEvent event = CloudEventBuilder.v1(Data.V1_MIN)
            .withData("application/json", myPojoSerialized.getBytes())
            .build();

        PojoCloudEventData<MyPojo> mappedData = CloudEventUtils.mapData(
            event,
            mapper
        );
        assertThat(mappedData)
            .isNotNull()
            .extracting(PojoCloudEventData::getValue)
            .isEqualTo(myPojo);
    }

    @ParameterizedTest
    @MethodSource("getPojoMappers")
    public void testWithJson(PojoCloudEventDataMapper<MyPojo> mapper) {

        CloudEvent event = CloudEventBuilder.v1(Data.V1_MIN)
            .withData("application/json", new JsonCloudEventData(myPojoJson))
            .build();

        PojoCloudEventData<MyPojo> mappedData = CloudEventUtils.mapData(
            event,
            mapper
        );
        assertThat(mappedData)
            .isNotNull()
            .extracting(PojoCloudEventData::getValue)
            .isEqualTo(myPojo);
    }

    private static Stream<Arguments> getPojoMappers() {
        final ObjectMapper objectMapper = new ObjectMapper();
        return Stream.of(
            Arguments.of(PojoCloudEventDataMapper.from(objectMapper, new TypeReference<MyPojo>() {})),
            Arguments.of(PojoCloudEventDataMapper.from(objectMapper, MyPojo.class))
        );
    }

}
