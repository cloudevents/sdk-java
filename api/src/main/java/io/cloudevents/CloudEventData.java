package io.cloudevents;

import java.util.function.Function;

/**
 * Interface that defines a container for CloudEvent data.
 *
 * Every CloudEvent data has
 */
public interface CloudEventData {

    /**
     * @return this CloudEventData, represented as bytes. Note: this operation may be expensive, depending on the internal representation of data
     */
    byte[] toBytes();

    //TBD
    <T> T to(Function<Object, T> mapper);

}
