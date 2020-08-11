package io.cloudevents.core;

/**
 * This interface extends the {@link io.cloudevents.CloudEvent} interface to provide additional
 * functionalities, based on the capabilities of the cloudevents-core package
 */
public interface CloudEvent extends io.cloudevents.CloudEvent {

    /**
     * An implementation of CloudEvent may contain an object which is then serialized to a byte array representation
     *
     * @return the raw content of the CloudEvent instance
     */
    Object getRawData();

}
