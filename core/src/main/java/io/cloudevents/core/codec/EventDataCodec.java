package io.cloudevents.core.codec;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.format.EventDeserializationException;
import io.cloudevents.core.format.EventSerializationException;

import javax.annotation.ParametersAreNonnullByDefault;

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
    byte[] serialize(String dataContentType, Object data) throws DataSerializationException, IllegalArgumentException;

    /**
     * Deserialize a byte array to a {@link CloudEvent}.
     *
     * @param data the serialized event.
     * @return the deserialized event.
     * @throws EventDeserializationException if something goes wrong during deserialization.
     * @throws IllegalArgumentException      if this codec cannot deserialize the provided data
     */
    <T> T deserialize(String dataContentType, Object data, Class<T> c) throws DataDeserializationException, IllegalArgumentException;

    boolean canDeserialize(String dataContentType);

    boolean canSerialize(String dataContentType);

}
