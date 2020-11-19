package io.cloudevents.core.data;

import io.cloudevents.CloudEventData;
import io.cloudevents.rw.CloudEventRWException;

import java.util.Objects;

public class PojoCloudEventData<T> implements CloudEventData {

    /**
     * Interface defining a conversion from T to byte array. This is similar to {@link java.util.function.Function}
     * but it allows checked exceptions.
     *
     * @param <T> the source type of the conversion
     */
    @FunctionalInterface
    public interface ToBytes<T> {
        byte[] convert(T data) throws Exception;
    }

    private final T value;
    private byte[] memoizedValue;
    private final ToBytes<T> mapper;

    private PojoCloudEventData(T value, byte[] memoizedValue, ToBytes<T> mapper) {
        Objects.requireNonNull(value);
        if (memoizedValue == null && mapper == null) {
            throw new NullPointerException("You must provide the serialized data value or a mapper");
        }
        this.value = value;
        this.memoizedValue = memoizedValue;
        this.mapper = mapper;
    }

    protected PojoCloudEventData(T value, ToBytes<T> mapper) {
        this(value, null, mapper);
    }

    protected PojoCloudEventData(T value, byte[] memoizedValue) {
        this(value, memoizedValue, null);
    }

    public T getValue() {
        return value;
    }

    @Override
    public byte[] toBytes() {
        if (this.memoizedValue == null) {
            try {
                this.memoizedValue = mapper.convert(this.value);
            } catch (Exception e) {
                throw CloudEventRWException.newDataConversion(e, value.getClass().toString(), "byte[]");
            }
        }
        return this.memoizedValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PojoCloudEventData<?> that = (PojoCloudEventData<?>) o;
        return Objects.equals(getValue(), that.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getValue());
    }

    /**
     * Wrap the provided data in a {@link PojoCloudEventData} serializable by the provided mapper.
     */
    public static <T> PojoCloudEventData<T> wrap(T data, ToBytes<T> mapper) {
        return new PojoCloudEventData<>(data, mapper);
    }

    /**
     * Wrap the provided data and its serialized value in a {@link PojoCloudEventData}.
     */
    public static <T> PojoCloudEventData<T> wrap(T data, byte[] serializedData) {
        return new PojoCloudEventData<>(data, serializedData);
    }
}
