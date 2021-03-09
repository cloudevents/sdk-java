package io.cloudevents.protobuf;

import com.google.protobuf.Message;
import io.cloudevents.CloudEventData;

/**
 * A {@link CloudEventData} that supports access to a protocol
 * buffer message.
 */
public interface ProtoCloudEventData extends CloudEventData {

    Message getMessage();

}
