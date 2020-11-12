package io.cloudevents.jackson;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cloudevents.CloudEventData;
import io.cloudevents.rw.CloudEventDataMapper;
import io.cloudevents.rw.CloudEventRWException;
import java.util.List;

public class PojoCloudEventDataMapper<T> implements CloudEventDataMapper<PojoCloudEventData<T>> {

    private final ObjectMapper mapper;
    private final JavaType target;

    private PojoCloudEventDataMapper(ObjectMapper mapper, JavaType target) {
        this.mapper = mapper;
        this.target = target;
    }

    @Override
    public PojoCloudEventData<T> map(CloudEventData data) throws CloudEventRWException {
        // Best case, event is already from json
        if (data instanceof JsonCloudEventData) {
            JsonNode node = ((JsonCloudEventData) data).getNode();
            T value;
            try {
                value = this.mapper.convertValue(node, target);
            } catch (Exception e) {
                throw CloudEventRWException.newDataConversion(e, JsonNode.class.toString(), target.getTypeName());
            }
            return new PojoCloudEventData<>(mapper, value);
        }

        // Worst case, deserialize from bytes
        T value;
        byte[] bytes = data.toBytes();
        try {
            value = this.mapper.readValue(bytes, this.target);
        } catch (Exception e) {
            throw CloudEventRWException.newDataConversion(e, byte[].class.toString(), target.getTypeName());
        }
        return new PojoCloudEventData<>(mapper, value, bytes);
    }

    /**
     * Creates a {@link PojoCloudEventDataMapper} mapping {@link CloudEventData} into {@link PojoCloudEventData}&lt;T&gt;.
     *
     * <p>
     * When working with generic types (e.g. {@link List}&lt{@link String}&gt),
     * it's better to use {@link PojoCloudEventDataMapper#from(ObjectMapper, TypeReference)}.
     * </p>
     *
     * @param mapper {@link ObjectMapper} used for POJO deserialization
     * @param target target type as {@link Class}&lt;T&gt;
     * @param <T> POJO Type
     * @return {@link CloudEventDataMapper}
     */
    public static <T> PojoCloudEventDataMapper<T> from(ObjectMapper mapper, Class<T> target) {
        return new PojoCloudEventDataMapper<>(mapper, mapper.getTypeFactory().constructType(target));
    }

    /**
     * Creates a {@link PojoCloudEventDataMapper} mapping {@link CloudEventData} into {@link PojoCloudEventData}&lt;T&gt;.
     *
     * <p>
     * This overload is more suitable for mapping generic objects (e.g. {@link List}&lt{@link String}&gt),
     * as opposed to {@link PojoCloudEventDataMapper#from(ObjectMapper, Class)}.
     * </p>
     *
     * @param mapper {@link ObjectMapper} used for POJO deserialization
     * @param target target type as {@link TypeReference}&lt;T&gt;
     * @param <T> POJO Type
     * @return {@link CloudEventDataMapper}
     */
    public static <T> PojoCloudEventDataMapper<T> from(ObjectMapper mapper, TypeReference<T> target) {
        return new PojoCloudEventDataMapper<>(mapper, mapper.getTypeFactory().constructType(target));
    }

}
