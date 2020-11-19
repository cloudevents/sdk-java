package io.cloudevents.core.data;

import io.cloudevents.CloudEventData;

import java.util.Arrays;
import java.util.Objects;

public class BytesCloudEventData implements CloudEventData {

    private final byte[] value;

    /**
     * @deprecated use {@link BytesCloudEventData#wrap(byte[])}
     */
    public BytesCloudEventData(byte[] value) {
        Objects.requireNonNull(value);
        this.value = value;
    }

    @Override
    public byte[] toBytes() {
        return this.value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BytesCloudEventData that = (BytesCloudEventData) o;
        return Arrays.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(value);
    }

    @Override
    public String toString() {
        return "BytesCloudEventData{" +
            "value=" + Arrays.toString(value) +
            '}';
    }

    /**
     * @param value byte array to wrap
     * @return byte array wrapped in a {@link BytesCloudEventData}, which implements {@link CloudEventData}.
     */
    public BytesCloudEventData wrap(byte[] value) {
        return new BytesCloudEventData(value);
    }
}
