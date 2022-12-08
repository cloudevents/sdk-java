package io.cloudevents.core.mock;

import io.cloudevents.CloudEventData;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class MyCloudEventData implements CloudEventData {

    private final int value;

    public MyCloudEventData(int value) {
        this.value = value;
    }

    @Override
    public byte[] toBytes() {
        return Integer.toString(value).getBytes(StandardCharsets.UTF_8);
    }

    public int getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MyCloudEventData that = (MyCloudEventData) o;
        return getValue() == that.getValue();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getValue());
    }

    public static MyCloudEventData fromStringBytes(byte[] bytes) {
        return new MyCloudEventData(Integer.valueOf(new String(bytes)));
    }

}
