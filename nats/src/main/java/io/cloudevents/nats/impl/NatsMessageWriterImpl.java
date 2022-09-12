package io.cloudevents.nats.impl;

import io.cloudevents.CloudEventData;
import io.cloudevents.SpecVersion;
import io.cloudevents.core.format.EventFormat;
import io.cloudevents.core.message.MessageWriter;
import io.cloudevents.core.v1.CloudEventV1;
import io.cloudevents.rw.CloudEventContextWriter;
import io.cloudevents.rw.CloudEventRWException;
import io.cloudevents.rw.CloudEventWriter;
import io.nats.client.Message;
import io.nats.client.impl.Headers;
import io.nats.client.impl.NatsMessage;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class NatsMessageWriterImpl implements MessageWriter<CloudEventWriter<Message>, Message>, CloudEventWriter<Message>  {
    private final NatsMessage.Builder builder = NatsMessage.builder();
    private final Headers headers = new Headers();
    private final String subject;

    public NatsMessageWriterImpl(@Nullable String subject) {
        this.subject = subject;
        if (subject != null) {
            builder.subject(subject);
        }
    }

    @Override
    public CloudEventContextWriter withContextAttribute(String name, String value) throws CloudEventRWException {
        // if a subject wasn't passed, take the subject of the cloud event and make it the subject
        if (CloudEventV1.SUBJECT.equals(name) && subject == null) {
            builder.subject(value);
        }

        headers.add(NatsHeaders.headerMapper(name), NatsHeaders.escapeHeaderValue(value));
        return this;
    }

    @Override
    public Message end(CloudEventData data) throws CloudEventRWException {
        headers.put(NatsHeaders.SPEC_VERSION, SpecVersion.V1.toString());
        return builder
            .headers(headers)
            .data(data.toBytes()).build();
    }

    @Override
    public Message end() throws CloudEventRWException {
        return builder.build();
    }

    @Override
    public CloudEventWriter<Message> create(SpecVersion version) throws CloudEventRWException {
        return this;
    }

    @Override
    public Message setEvent(EventFormat format, byte[] value) throws CloudEventRWException {
        // force the header to be this format. Headers will be ignored if the server can't support them
        headers.add(NatsHeaders.CONTENT_TYPE, format.serializedContentType());
        builder.data(value).headers(headers);
        return builder.build();
    }
}
