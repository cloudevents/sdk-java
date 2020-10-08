package io.cloudevents;

/**
 * Interface that defines a wrapper for CloudEvent data.
 */
public interface CloudEventData {

    /**
     * @return this CloudEventData, represented as bytes. Note: this operation may be expensive, depending on the internal representation of data
     */
    byte[] toBytes();

}
