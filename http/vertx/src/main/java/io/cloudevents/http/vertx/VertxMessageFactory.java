package io.cloudevents.http.vertx;

import io.cloudevents.core.message.MessageReader;
import io.cloudevents.core.message.MessageWriter;
import io.cloudevents.core.message.impl.GenericStructuredMessageReader;
import io.cloudevents.core.message.impl.MessageUtils;
import io.cloudevents.http.vertx.impl.BinaryVertxMessageReaderImpl;
import io.cloudevents.http.vertx.impl.CloudEventsHeaders;
import io.cloudevents.http.vertx.impl.VertxWebClientRequestMessageWriterImpl;
import io.cloudevents.http.vertx.impl.VertxHttpServerResponseMessageWriterImpl;
import io.cloudevents.rw.CloudEventWriter;
import io.vertx.core.*;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.HttpResponse;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * This class provides a collection of methods to create {@link io.cloudevents.core.message.MessageReader}
 * and {@link io.cloudevents.core.message.MessageWriter} for Vert.x HTTP Server and Web Client.
 */
@ParametersAreNonnullByDefault
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
            sv -> new BinaryVertxMessageReaderImpl(sv, headers, body)
        );
    }

    /**
     * Build a {@link MessageReader} starting from an {@link HttpServerRequest}
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
     * @param request
     * @param handler
     * @see #createReader(HttpServerRequest)
     */
    public static void createReader(HttpServerRequest request, Handler<AsyncResult<MessageReader>> handler) {
        createReader(request).onComplete(handler);
    }

    /**
     * Build a {@link MessageReader} starting from an {@link io.vertx.ext.web.client.HttpResponse}
     *
     * @param response
     * @return
     */
    public static MessageReader createReader(HttpResponse<Buffer> response) {
        return createReader(response.headers(), response.body());
    }

    /**
     * Creates a {@link MessageWriter} that can write both structured and binary messages to a {@link HttpServerResponse}.
     * When the writer finished to write the {@link MessageReader}, the response is ended with {@link HttpServerResponse#end(io.vertx.core.buffer.Buffer)}
     *
     * @param res the response to write
     * @return the message writer
     */
    public static MessageWriter<CloudEventWriter<HttpServerResponse>, HttpServerResponse> createWriter(HttpServerResponse res) {
        return new VertxHttpServerResponseMessageWriterImpl(res);
    }

    /**
     * Creates a {@link MessageWriter} that can write both structured and binary messages to a {@link io.vertx.ext.web.client.HttpRequest}.
     * When the writer finished to write the {@link MessageReader}, the request is sent with {@link io.vertx.ext.web.client.HttpRequest#sendBuffer(Buffer)}
     * and it returns the {@link Future} containing the response.
     *
     * @param req the request to write
     * @return the message writer
     */
    public static MessageWriter<CloudEventWriter<Future<HttpResponse<Buffer>>>, Future<HttpResponse<Buffer>>> createWriter(HttpRequest<Buffer> req) {
        return new VertxWebClientRequestMessageWriterImpl(req);
    }
}
