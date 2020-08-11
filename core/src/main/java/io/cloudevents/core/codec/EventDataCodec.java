package io.cloudevents.core.codec;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.format.EventSerializationException;
import io.cloudevents.lang.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * This interface represents how to encode an event data to bytes, in order to write the payload on the wire.
 */
@ParametersAreNonnullByDefault
public interface EventDataCodec {

    /**
     * Serialize a {@link CloudEvent} to a byte array.
     *
     * @param data the data to serialize.
     * @return the byte representation of the provided event.
     * @throws EventSerializationException if something goes wrong during serialization.
     * @throws IllegalArgumentException    if this codec cannot serialize the provided data
     */
    byte[] serialize(@Nullable String dataContentType, Object data) throws DataSerializationException, IllegalArgumentException;

    /**
     * @param dataContentType the eventual content type to serialize
     * @return true if this event codec instance can serialize the provided content type.
     */
    boolean canSerialize(@Nullable String dataContentType);

}
