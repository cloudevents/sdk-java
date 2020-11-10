package io.cloudevents.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cloudevents.CloudEventData;
import io.cloudevents.rw.CloudEventRWException;

public class PojoCloudEventData<T> implements CloudEventData {

    private final ObjectMapper mapper;
    private byte[] memoizedValue;
    private final T value;

    protected PojoCloudEventData(ObjectMapper mapper, T value) {
        this(mapper, value, null);
    }

    protected PojoCloudEventData(ObjectMapper mapper, T value, byte[] memoizedValue) {
        this.mapper = mapper;
        this.value = value;
        this.memoizedValue = memoizedValue;
    }

    public T getValue() {
        return value;
    }

    @Override
    public byte[] toBytes() {
        if (this.memoizedValue == null) {
            try {
                this.memoizedValue = mapper.writeValueAsBytes(value);
            } catch (JsonProcessingException e) {
                throw CloudEventRWException.newDataConversion(e, "byte[]");
            }
        }
        return this.memoizedValue;
    }
}
