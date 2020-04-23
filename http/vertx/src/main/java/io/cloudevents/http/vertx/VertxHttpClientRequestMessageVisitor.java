package io.cloudevents.http.vertx;

import io.cloudevents.http.vertx.impl.VertxHttpClientRequestMessageVisitorImpl;
import io.cloudevents.message.BinaryMessageVisitor;
import io.cloudevents.message.MessageVisitor;
import io.vertx.core.http.HttpClientRequest;

/**
 * Visitor for {@link io.cloudevents.message.Message} that can write both structured and binary messages to a {@link HttpClientRequest}.
 * When the visit ends, the request is ended with {@link HttpClientRequest#end(io.vertx.core.buffer.Buffer)}
 */
public interface VertxHttpClientRequestMessageVisitor extends MessageVisitor<VertxHttpClientRequestMessageVisitor, HttpClientRequest>, BinaryMessageVisitor<HttpClientRequest> {

    static VertxHttpClientRequestMessageVisitor create(HttpClientRequest req) {
        return new VertxHttpClientRequestMessageVisitorImpl(req);
    }

}
