package io.cloudevents.http.vertx.impl;

import io.cloudevents.SpecVersion;
import io.cloudevents.format.EventFormat;
import io.cloudevents.http.vertx.VertxHttpServerResponseMessageVisitor;
import io.cloudevents.message.MessageVisitException;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerResponse;

public class VertxHttpServerResponseMessageVisitorImpl implements VertxHttpServerResponseMessageVisitor {

    private final HttpServerResponse response;

    public VertxHttpServerResponseMessageVisitorImpl(HttpServerResponse response) {
        this.response = response;
    }

    // Binary visitor factory

    @Override
    public VertxHttpServerResponseMessageVisitor createBinaryMessageVisitor(SpecVersion version) {
        this.response.putHeader(CloudEventsHeaders.SPEC_VERSION, version.toString());
        return this;
    }

    // Binary visitor

    @Override
    public void setAttribute(String name, String value) throws MessageVisitException {
        this.response.putHeader(CloudEventsHeaders.ATTRIBUTES_TO_HEADERS.get(name), value);
    }

    @Override
    public void setExtension(String name, String value) throws MessageVisitException {
        this.response.putHeader("ce-" + name, value);
    }

    @Override
    public void setBody(byte[] value) throws MessageVisitException {
        this.response.end(Buffer.buffer(value));
    }

    @Override
    public HttpServerResponse end() {
        return this.response;
    }

    // Structured visitor

    @Override
    public HttpServerResponse setEvent(EventFormat format, byte[] value) throws MessageVisitException {
        this.response.putHeader(HttpHeaders.CONTENT_TYPE, format.serializedContentType());
        this.response.end(Buffer.buffer(value));
        return this.response;
    }
}
