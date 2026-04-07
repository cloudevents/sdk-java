package io.cloudevents.jackson;

import io.cloudevents.CloudEventData;
import io.cloudevents.core.data.PojoCloudEventData;
import io.cloudevents.rw.CloudEventDataMapper;
import io.cloudevents.rw.CloudEventRWException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;

/**
 * This class implements a {@link CloudEventDataMapper} that maps any input {@link CloudEventData} to the specified target type using the Jackson {@link JsonMapper}.
 *
 * @param <T> the target type of the conversion
 */
public class PojoCloudEventDataMapper<T> implements CloudEventDataMapper<PojoCloudEventData<T>> {

    private final JsonMapper mapper;
    private final JavaType target;

    private PojoCloudEventDataMapper(JsonMapper mapper, JavaType target) {
        this.mapper = mapper;
        this.target = target;
    }

    @Override
    public PojoCloudEventData<T> map(CloudEventData data) throws CloudEventRWException {
        // Best case, event is already from json
        if (data instanceof JsonCloudEventData eventData) {
            JsonNode node = eventData.getNode();
            T value;
            try {
                value = this.mapper.convertValue(node, target);
            } catch (Exception e) {
                throw CloudEventRWException.newDataConversion(e, JsonNode.class.toString(), target.getTypeName());
            }
            return PojoCloudEventData.wrap(value, mapper::writeValueAsBytes);
        }

        // Worst case, deserialize from bytes
        T value;
        byte[] bytes = data.toBytes();
        try {
            value = this.mapper.readValue(bytes, this.target);
        } catch (Exception e) {
            throw CloudEventRWException.newDataConversion(e, byte[].class.toString(), target.getTypeName());
        }
        return PojoCloudEventData.wrap(value, v -> bytes);
    }

    /**
     * Creates a {@link PojoCloudEventDataMapper} mapping {@link CloudEventData} into {@link PojoCloudEventData}&lt;T&gt;
     * using a Jackson {@link JsonMapper}.
     *
     * <p>
     * When working with generic types (e.g. {@link List}&lt;{@link String}&gt;),
     * it's better to use {@link PojoCloudEventDataMapper#from(JsonMapper, TypeReference)}.
     * </p>
     *
     * @param mapper {@link JsonMapper} used for POJO deserialization
     * @param target target type as {@link Class}&lt;T&gt;
     * @param <T> POJO Type
     * @return {@link CloudEventDataMapper}
     */
    public static <T> PojoCloudEventDataMapper<T> from(JsonMapper mapper, Class<T> target) {
        return new PojoCloudEventDataMapper<>(mapper, mapper.getTypeFactory().constructType(target));
    }

    /**
     * Creates a {@link PojoCloudEventDataMapper} mapping {@link CloudEventData} into {@link PojoCloudEventData}&lt;T&gt;
     * using a Jackson {@link JsonMapper}.
     *
     * <p>
     * This overload is more suitable for mapping generic objects (e.g. {@link List}&lt;{@link String}&gt;),
     * as opposed to {@link PojoCloudEventDataMapper#from(JsonMapper, Class)}.
     * </p>
     *
     * @param mapper {@link JsonMapper} used for POJO deserialization
     * @param target target type as {@link TypeReference}&lt;T&gt;
     * @param <T> POJO Type
     * @return {@link CloudEventDataMapper}
     */
    public static <T> PojoCloudEventDataMapper<T> from(JsonMapper mapper, TypeReference<T> target) {
        return new PojoCloudEventDataMapper<>(mapper, mapper.getTypeFactory().constructType(target));
    }

}
