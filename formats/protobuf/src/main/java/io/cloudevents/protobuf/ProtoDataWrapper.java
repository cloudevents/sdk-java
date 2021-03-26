package io.cloudevents.protobuf;

import com.google.protobuf.Message;

class ProtoDataWrapper implements ProtoCloudEventData {

    private final Message protoMessage;

    ProtoDataWrapper(Message protoMessage) {
        this.protoMessage = protoMessage;
    }
    @Override
    public Message getMessage() {
        return protoMessage;
    }

    @Override
    public byte[] toBytes() {
        return protoMessage.toByteArray();
    }
}
