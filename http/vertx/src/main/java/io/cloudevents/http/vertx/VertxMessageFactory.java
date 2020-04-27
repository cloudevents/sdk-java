package io.cloudevents.http.vertx;

import io.cloudevents.http.vertx.impl.BinaryVertxMessageImpl;
import io.cloudevents.http.vertx.impl.CloudEventsHeaders;
import io.cloudevents.message.Message;
import io.cloudevents.message.impl.GenericStructuredMessage;
import io.cloudevents.message.impl.MessageUtils;
import io.cloudevents.message.impl.UnknownEncodingMessage;
import io.vertx.core.*;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerRequest;

/**
 * Interface representing a Message implementation using Vert.x types
 */
public interface VertxMessageFactory {

    /**
     * Create a new Message using Vert.x headers and body.
     *
     * @param headers Http headers
     * @param body    nullable buffer of the body
     * @return a Message implementation with potentially an unknown encoding
     * @throws IllegalArgumentException If, in case of binary mode, the spec version is invalid
     */
    static Message create(MultiMap headers, Buffer body) throws IllegalArgumentException {
        return MessageUtils.parseStructuredOrBinaryMessage(
            () -> headers.get(HttpHeaders.CONTENT_TYPE),
            format -> new GenericStructuredMessage(format, body.getBytes()),
            () -> headers.get(CloudEventsHeaders.SPEC_VERSION),
            sv -> new BinaryVertxMessageImpl(sv, headers, body),
            UnknownEncodingMessage::new
        );
    }

    /**
     * Build a message starting from an {@link HttpServerRequest}
     *
     * @param request
     * @return
     */
    static Future<Message> fromHttpServerRequest(HttpServerRequest request) {
        Promise<Message> prom = Promise.promise();

        request.exceptionHandler(prom::tryFail);
        request.bodyHandler(b -> {
            try {
                prom.complete(create(request.headers(), b));
            } catch (IllegalArgumentException e) {
                prom.fail(e);
            }
        });
        return prom.future();
    }

    /**
     * Like {@link VertxMessageFactory#fromHttpServerRequest(HttpServerRequest)}
     *
     * @param request
     * @param handler
     */
    static void fromHttpServerRequest(HttpServerRequest request, Handler<AsyncResult<Message>> handler) {
        fromHttpServerRequest(request).onComplete(handler);
    }

    /**
     * Build a message starting from an {@link HttpClientResponse}
     *
     * @param request
     * @return
     */
    static Future<Message> fromHttpClientResponse(HttpClientResponse request) {
        Promise<Message> prom = Promise.promise();

        request.exceptionHandler(prom::tryFail);
        request.bodyHandler(b -> {
            try {
                prom.complete(create(request.headers(), b));
            } catch (IllegalArgumentException e) {
                prom.fail(e);
            }
        });
        return prom.future();
    }

    /**
     * Like {@link VertxMessageFactory#fromHttpClientResponse(HttpClientResponse)}
     *
     * @param response
     * @param handler
     */
    static void fromHttpClientResponse(HttpClientResponse response, Handler<AsyncResult<Message>> handler) {
        fromHttpClientResponse(response).onComplete(handler);
    }

}
