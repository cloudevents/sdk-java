package io.cloudevents.nats;

import io.cloudevents.CloudEvent;
import io.cloudevents.SpecVersion;
import io.cloudevents.core.message.MessageReader;
import io.cloudevents.core.message.MessageWriter;
import io.cloudevents.core.message.impl.GenericStructuredMessageReader;
import io.cloudevents.core.message.impl.MessageUtils;
import io.cloudevents.nats.impl.NatsMessageReaderImpl;
import io.cloudevents.nats.impl.NatsMessageWriterImpl;
import io.cloudevents.nats.impl.NatsHeaders;
import io.cloudevents.rw.CloudEventRWException;
import io.cloudevents.rw.CloudEventWriter;
import io.nats.client.Connection;
import io.nats.client.Message;
import io.nats.client.impl.Headers;

import javax.annotation.Nullable;

/**
 * This is the main NATS Message factory that will read and write CloudEvents into NATS messages. Your code is responsible for publishing them, so you
 * would do something like:
 *
 * nats.connection.publish(NatsMessageFactory.createWriter(event))
 *
 * and correspondingly to read, you would do:
 *
 * nats.connection.createDispatcher().subscribe("some-topic", (msg) -&amp; process(NatsMessageFactory.createReader(msg).toEvent()));
 */
public class NatsMessageFactory {
    private NatsMessageFactory() {
    }

    /**
     * Create a {@link io.cloudevents.core.message.MessageReader} to read {@link Message}.
     *
     * @param message the message to convert to {@link io.cloudevents.core.message.MessageReader}
     * @return the new {@link io.cloudevents.core.message.MessageReader}
     * @throws CloudEventRWException if something goes wrong while resolving the {@link SpecVersion} or if the message has unknown encoding
     */
    public static MessageReader createReader(Message message) throws CloudEventRWException {
        return createReader(message.getHeaders(), message.getData());
    }

    /**
     * @see #createReader(Message)
     *
     * @param headers - a NATS Headers object
     * @param payload - a binary payload, usually from a NATS message
     */
    public static MessageReader createReader(Headers headers, byte[] payload) throws CloudEventRWException {
        return MessageUtils.parseStructuredOrBinaryMessage(
            () -> headers.getFirst(NatsHeaders.CONTENT_TYPE),
            format -> new GenericStructuredMessageReader(format, payload),
            () -> headers.getFirst(NatsHeaders.SPEC_VERSION),
            sv -> new NatsMessageReaderImpl(sv, headers, payload)
        );
    }

    /**
     * Returns a message writer for NATS messages. The subject in the Message will either be null or it will pick up the subject
     * from your CloudEvent.
     *
     * @return a Message object
     */
    public static MessageWriter<CloudEventWriter<Message>, Message> createWriter() {
        return createWriter(null); // infer subject from cloud event subject
    }

    /**
     * Returns a message writer for NATS messages.
     *
     * @param subject - the subject for the Message. It can be null in which case you must set it yourself.
     * @return a Message object
     */
    public static MessageWriter<CloudEventWriter<Message>, Message> createWriter(@Nullable String subject) {
        return new NatsMessageWriterImpl(subject);
    }}
