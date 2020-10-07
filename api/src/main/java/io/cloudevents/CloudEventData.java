package io.cloudevents;

import java.util.function.Function;

public interface CloudEventData {

    byte[] toBytes();

    <T> T to(Function<Object, T> mapper);

}
