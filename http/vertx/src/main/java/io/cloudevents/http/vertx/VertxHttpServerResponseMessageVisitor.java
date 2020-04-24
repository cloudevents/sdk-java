package io.cloudevents.http.vertx;

import io.cloudevents.http.vertx.impl.VertxHttpServerResponseMessageVisitorImpl;
import io.cloudevents.message.BinaryMessageVisitor;
import io.cloudevents.message.MessageVisitor;
import io.vertx.core.http.HttpServerResponse;

/**
 * Visitor for {@link io.cloudevents.message.Message} that can write both structured and binary messages to a {@link HttpServerResponse}.
 * When the visit ends, the request is ended with {@link HttpServerResponse#end(io.vertx.core.buffer.Buffer)}
 */
public interface VertxHttpServerResponseMessageVisitor extends MessageVisitor<VertxHttpServerResponseMessageVisitor, HttpServerResponse>, BinaryMessageVisitor<HttpServerResponse> {

    static VertxHttpServerResponseMessageVisitor create(HttpServerResponse res) {
        return new VertxHttpServerResponseMessageVisitorImpl(res);
    }

}
