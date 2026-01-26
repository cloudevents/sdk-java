package io.cloudevents.jackson;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.CloudEventUtils;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.core.data.PojoCloudEventData;
import io.cloudevents.core.test.Data;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.node.JsonNodeFactory;

import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class PojoCloudEventDataMapperTest {

    private final JsonNode myPojoJson = JsonNodeFactory.instance.objectNode().put("a", 10).put("b", "Hello World!");
    private final String myPojoSerialized = myPojoJson.toString();
    private final MyPojo myPojo = new MyPojo(10, "Hello World!");

    @ParameterizedTest
    @MethodSource("getPojoMappers")
    void testWithBytes(PojoCloudEventDataMapper<MyPojo> mapper) {
        CloudEvent event = CloudEventBuilder.v1(Data.V1_MIN)
            .withData("application/json", myPojoSerialized.getBytes(StandardCharsets.UTF_8))
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
    void testWithJson(PojoCloudEventDataMapper<MyPojo> mapper) {
        CloudEvent event = CloudEventBuilder.v1(Data.V1_MIN)
            .withData("application/json", JsonCloudEventData.wrap(myPojoJson))
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
        final JsonMapper objectMapper = new JsonMapper();
        return Stream.of(
            Arguments.of(PojoCloudEventDataMapper.from(objectMapper, new TypeReference<MyPojo>() {})),
            Arguments.of(PojoCloudEventDataMapper.from(objectMapper, MyPojo.class))
        );
    }

}
