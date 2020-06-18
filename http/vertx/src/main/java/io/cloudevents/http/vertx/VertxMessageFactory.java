package io.cloudevents.http.vertx;

import io.cloudevents.core.message.MessageReader;
import io.cloudevents.core.message.MessageWriter;
import io.cloudevents.core.message.impl.GenericStructuredMessageReader;
import io.cloudevents.core.message.impl.MessageUtils;
import io.cloudevents.core.message.impl.UnknownEncodingMessageReader;
import io.cloudevents.http.vertx.impl.BinaryVertxMessageReaderImpl;
import io.cloudevents.http.vertx.impl.CloudEventsHeaders;
import io.cloudevents.http.vertx.impl.VertxHttpClientRequestMessageWriterImpl;
import io.cloudevents.http.vertx.impl.VertxHttpServerResponseMessageWriterImpl;
import io.cloudevents.rw.CloudEventWriter;
import io.vertx.core.*;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.*;

/**
 * This class provides a collection of methods to create {@link io.cloudevents.core.message.MessageReader}
 * and {@link io.cloudevents.core.message.MessageWriter} for Vert.x HTTP Client and Server.
 */
public final class VertxMessageFactory {

    private VertxMessageFactory() {
    }

    /**
     * Create a new Message using Vert.x headers and body.
     *
     * @param headers Http headers
     * @param body    nullable buffer of the body
     * @return a Message implementation with potentially an unknown encoding
     * @throws IllegalArgumentException If, in case of binary mode, the spec version is invalid
     */
    public static MessageReader createReader(MultiMap headers, Buffer body) throws IllegalArgumentException {
        return MessageUtils.parseStructuredOrBinaryMessage(
            () -> headers.get(HttpHeaders.CONTENT_TYPE),
            format -> new GenericStructuredMessageReader(format, body.getBytes()),
            () -> headers.get(CloudEventsHeaders.SPEC_VERSION),
            sv -> new BinaryVertxMessageReaderImpl(sv, headers, body),
            UnknownEncodingMessageReader::new
        );
    }

    /**
     * Build a message starting from an {@link HttpServerRequest}
     *
     * @param request
     * @return
     */
    public static Future<MessageReader> createReader(HttpServerRequest request) {
        Promise<MessageReader> prom = Promise.promise();

        request.exceptionHandler(prom::tryFail);
        request.bodyHandler(b -> {
            try {
                prom.complete(createReader(request.headers(), b));
            } catch (IllegalArgumentException e) {
                prom.fail(e);
            }
        });
        return prom.future();
    }

    /**
     * Like {@link this#createReader(HttpServerRequest)}
     *
     * @param request
     * @param handler
     */
    public static void createReader(HttpServerRequest request, Handler<AsyncResult<MessageReader>> handler) {
        createReader(request).onComplete(handler);
    }

    /**
     * Build a message starting from an {@link HttpClientResponse}
     *
     * @param request
     * @return
     */
    public static Future<MessageReader> createReader(HttpClientResponse request) {
        Promise<MessageReader> prom = Promise.promise();

        request.exceptionHandler(prom::tryFail);
        request.bodyHandler(b -> {
            try {
                prom.complete(createReader(request.headers(), b));
            } catch (IllegalArgumentException e) {
                prom.fail(e);
            }
        });
        return prom.future();
    }

    /**
     * Like {@link this#createReader(HttpClientResponse)}
     *
     * @param response
     * @param handler
     */
    public static void createReader(HttpClientResponse response, Handler<AsyncResult<MessageReader>> handler) {
        createReader(response).onComplete(handler);
    }

    /**
     * Creates a {@link MessageWriter} that can write both structured and binary messages to a {@link HttpServerResponse}.
     * When the visit ends, the response is ended with {@link HttpServerResponse#end(io.vertx.core.buffer.Buffer)}
     *
     * @param res the response to write
     * @return the message writer
     */
    public static MessageWriter<CloudEventWriter<HttpServerResponse>, HttpServerResponse> createWriter(HttpServerResponse res) {
        return new VertxHttpServerResponseMessageWriterImpl(res);
    }

    /**
     * Creates a {@link MessageWriter} that can write both structured and binary messages to a {@link HttpClientRequest}.
     * When the visit ends, the response is ended with {@link HttpClientRequest#end(io.vertx.core.buffer.Buffer)}
     *
     * @param req the request to write
     * @return the message writer
     */
    public static MessageWriter<CloudEventWriter<HttpClientRequest>, HttpClientRequest> createWriter(HttpClientRequest req) {
        return new VertxHttpClientRequestMessageWriterImpl(req);
    }
}
