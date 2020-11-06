package io.cloudevents.core.mock;

import io.cloudevents.CloudEventData;

public class MyCloudEventData implements CloudEventData {

    private final int value;

    public MyCloudEventData(int value) {
        this.value = value;
    }

    @Override
    public byte[] toBytes() {
        return Integer.toString(value).getBytes();
    }

    public int getValue() {
        return value;
    }

    public static MyCloudEventData fromStringBytes(byte[] bytes) {
        return new MyCloudEventData(Integer.valueOf(new String(bytes)));
    }
}
