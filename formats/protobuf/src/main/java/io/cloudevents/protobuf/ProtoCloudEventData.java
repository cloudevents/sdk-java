package io.cloudevents.protobuf;

import com.google.protobuf.Message;
import io.cloudevents.CloudEventData;

/**
 * A {@link CloudEventData} that supports access to a protocol
 * buffer message.
 */
public interface ProtoCloudEventData extends CloudEventData {

    /**
     * Gets the protobuf {@link Message} representation of this data.
     * @return The data as a {@link Message}
     */
    Message getMessage();

}
