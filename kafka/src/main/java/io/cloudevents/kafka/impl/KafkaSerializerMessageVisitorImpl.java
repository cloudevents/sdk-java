package io.cloudevents.kafka.impl;

import io.cloudevents.SpecVersion;
import org.apache.kafka.common.header.Headers;

public final class KafkaSerializerMessageVisitorImpl extends BaseKafkaMessageVisitorImpl<KafkaSerializerMessageVisitorImpl, byte[]> {

    public KafkaSerializerMessageVisitorImpl(Headers headers) {
        super(headers);
    }

    @Override
    public KafkaSerializerMessageVisitorImpl createBinaryMessageVisitor(SpecVersion version) {
        this.setAttribute("specversion", version.toString());
        return this;
    }

    @Override
    public byte[] end() {
        return this.value;
    }

}
