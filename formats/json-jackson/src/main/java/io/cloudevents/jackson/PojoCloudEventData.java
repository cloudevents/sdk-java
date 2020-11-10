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
//
//    /**
//     * Extract the data from the event. Be aware that this doesn't perform the mapping:
//     * to execute the mapping, you need to use {@link PojoCloudEventDataMapper}.
//     *
//     * @param event the event containing a data with {@link PojoCloudEventDataMapper}
//     * @param <T> the return value type
//     * @return null if there wasn't any data, otherwise the value casted to T
//     */
//    @SuppressWarnings("unchecked")
//    public static <T> T extractFromEvent(CloudEvent event) {
//        CloudEventData data = event.getData();
//        if (data == null) {
//            return null;
//        }
//        PojoCloudEventData pojoData;
//        try {
//            pojoData = (PojoCloudEventData) data;
//        } catch (ClassCastException e) {
//            throw new IllegalArgumentException("Provided event contains a CloudEventData not instance of PojoCloudEventData. Use the mapper PojoCloudEventDataMapper when you deserialize the event", e);
//        }
//        return (T)pojoData.getValue();
//    }
}
