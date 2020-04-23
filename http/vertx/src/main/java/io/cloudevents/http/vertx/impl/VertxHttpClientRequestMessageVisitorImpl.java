package io.cloudevents.http.vertx.impl;

import io.cloudevents.SpecVersion;
import io.cloudevents.format.EventFormat;
import io.cloudevents.http.vertx.VertxHttpClientRequestMessageVisitor;
import io.cloudevents.message.MessageVisitException;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpHeaders;

public class VertxHttpClientRequestMessageVisitorImpl implements VertxHttpClientRequestMessageVisitor {

    private final HttpClientRequest request;

    public VertxHttpClientRequestMessageVisitorImpl(HttpClientRequest request) {
        this.request = request;
    }

    // Binary visitor factory

    @Override
    public VertxHttpClientRequestMessageVisitor createBinaryMessageVisitor(SpecVersion version) {
        this.request.putHeader(CloudEventsHeaders.SPEC_VERSION, version.toString());
        return this;
    }

    // Binary visitor

    @Override
    public void setAttribute(String name, String value) throws MessageVisitException {
        this.request.putHeader(CloudEventsHeaders.ATTRIBUTES_TO_HEADERS.get(name), value);
    }

    @Override
    public void setExtension(String name, String value) throws MessageVisitException {
        this.request.putHeader("ce-" + name, value);
    }

    @Override
    public void setBody(byte[] value) throws MessageVisitException {
        this.request.end(Buffer.buffer(value));
    }

    @Override
    public HttpClientRequest end() {
        return this.request;
    }

    // Structured visitor

    @Override
    public HttpClientRequest setEvent(EventFormat format, byte[] value) throws MessageVisitException {
        this.request.putHeader(HttpHeaders.CONTENT_TYPE, format.serializedContentType());
        this.request.end(Buffer.buffer(value));
        return this.request;
    }
}
