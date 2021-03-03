package io.cloudevents.proto;

import com.google.protobuf.Message;
import io.cloudevents.CloudEventData;

public interface ProtoCloudEventData extends CloudEventData {

    Message getMessage();

}
