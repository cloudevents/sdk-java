package io.cloudevents.kafka.impl;

import io.cloudevents.format.EventFormat;
import io.cloudevents.message.BinaryMessageVisitor;
import io.cloudevents.message.MessageVisitException;
import io.cloudevents.message.MessageVisitor;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.header.internals.RecordHeader;

public abstract class BaseKafkaMessageVisitorImpl<S extends MessageVisitor<S, R> & BinaryMessageVisitor<R>, R> implements MessageVisitor<S, R>, BinaryMessageVisitor<R> {

    byte[] value;
    final Headers headers;

    public BaseKafkaMessageVisitorImpl(Headers headers) {
        this.headers = headers;
    }

    @Override
    public void setAttribute(String name, String value) throws MessageVisitException {
        headers.add(new RecordHeader(KafkaHeaders.ATTRIBUTES_TO_HEADERS.get(name), value.getBytes()));
    }

    @Override
    public void setExtension(String name, String value) throws MessageVisitException {
        headers.add(new RecordHeader(KafkaHeaders.CE_PREFIX + name, value.getBytes()));
    }

    @Override
    public void setBody(byte[] value) throws MessageVisitException {
        this.value = value;
    }

    @Override
    public R setEvent(EventFormat format, byte[] value) throws MessageVisitException {
        this.headers.add(new RecordHeader(KafkaHeaders.CONTENT_TYPE, format.serializedContentType().getBytes()));
        this.value = value;
        return this.end();
    }
}
