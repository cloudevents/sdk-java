package io.cloudevents.jackson;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cloudevents.CloudEventData;
import io.cloudevents.rw.CloudEventDataMapper;
import io.cloudevents.rw.CloudEventRWException;

public class PojoCloudEventDataMapper<T> implements CloudEventDataMapper {

    private final ObjectMapper mapper;
    private final TypeReference<T> target;

    private PojoCloudEventDataMapper(ObjectMapper mapper, TypeReference<T> target) {
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
                throw CloudEventRWException.newDataConversion(e, target.getType().toString());
            }
            return new PojoCloudEventData<>(mapper, value);
        }

        // Worst case, deserialize from bytes
        T value;
        byte[] bytes = data.toBytes();
        try {
            value = this.mapper.readValue(bytes, this.target);
        } catch (Exception e) {
            throw CloudEventRWException.newDataConversion(e, target.getType().toString());
        }
        return new PojoCloudEventData<>(mapper, value, bytes);
    }

    public static <T> PojoCloudEventDataMapper<T> from(ObjectMapper mapper, TypeReference<T> target) {
        return new PojoCloudEventDataMapper<>(mapper, target);
    }
}
