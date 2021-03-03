package io.cloudevents.proto;

import com.google.protobuf.InvalidProtocolBufferException;
import io.cloudevents.CloudEvent;
import io.cloudevents.CloudEventData;
import io.cloudevents.core.format.EventDeserializationException;
import io.cloudevents.core.format.EventFormat;
import io.cloudevents.core.format.EventSerializationException;
import io.cloudevents.rw.CloudEventDataMapper;

import java.util.Set;
import java.util.stream.Stream;

public final class ProtoFormat implements EventFormat {

    /**
     * Spec'd Content-Type for Protocol Buffer content
     */
    public static final String CONTENT_TYPE = "application/cloudevents+protobuf";


    @Override
    public byte[] serialize(CloudEvent event) throws EventSerializationException {

        ProtoCloudEvent protoEvent;

        if (event instanceof ProtoCloudEvent) {
            protoEvent = (ProtoCloudEvent) event;
        } else {
            protoEvent = convertEvent(event);
        }

        return protoEvent.getProtobufMessage().toByteArray();
    }

    @Override
    public CloudEvent deserialize(byte[] bytes) throws EventDeserializationException
    {
        io.cloudevents.v1.proto.CloudEvent protoEvent = null;

        try {
            // Attempt to parse the protobuf data
            protoEvent = io.cloudevents.v1.proto.CloudEvent.parseFrom(bytes);

            // Construct the wrapper and we're done.
            return new ProtoCloudEvent(protoEvent);

        } catch (InvalidProtocolBufferException e) {
            throw new EventDeserializationException(e);
        }


    }

    @Override
    public CloudEvent deserialize(byte[] bytes, CloudEventDataMapper<? extends CloudEventData> mapper) throws EventDeserializationException {
        return null;
    }

    @Override
    public String serializedContentType() {

        return CONTENT_TYPE;
    }

    // ------------------------------------------------------------------------------------


    /**
     * Creates a Protobuf representation of a CloudEvent
     * @param event The event to convert
     * @return A protobuf backed CloudEvent
     */
    private ProtoCloudEvent convertEvent(CloudEvent event) {

        ProtoEventWriter writer = new ProtoEventWriter();

        // Copy the attributes and extensions into the new event

        event.getAttributeNames()
            .stream()
            .forEach(x -> writer.withContextAttribute(x, event.getAttribute(x)));

        event.getExtensionNames()
            .stream()
            .forEach(x -> writer.withContextAttribute(x, event.getExtension(x)));

        return (ProtoCloudEvent) writer.end(event.getData());

    }
}
