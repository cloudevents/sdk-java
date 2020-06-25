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

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Consumer;

/**
 * This class provides a collection of methods to create {@link io.cloudevents.core.message.MessageReader}
 * and {@link io.cloudevents.core.message.MessageWriter} for Vert.x HTTP Client and Server.
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
            sv -> new BinaryVertxMessageReaderImpl(sv, headers, body),
            UnknownEncodingMessageReader::new
        );
    }

    /**
     * Build a {@link MessageReader} starting from an {@link HttpServerRequest}
     *
     * @param request
     * @return
     */
    public static Future<MessageReader> createReader(HttpServerRequest request) {
        return createReader(request.headers(), request::exceptionHandler, request::bodyHandler);
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
     * Build a {@link MessageReader} starting from an {@link HttpClientResponse}
     *
     * @param response
     * @return
     */
    public static Future<MessageReader> createReader(HttpClientResponse response) {
        return createReader(response.headers(), response::exceptionHandler, response::bodyHandler);
    }

    /**
     * @see #createReader(HttpClientResponse)
     *
     * @param response
     * @param handler
     */
    public static void createReader(HttpClientResponse response, Handler<AsyncResult<MessageReader>> handler) {
        createReader(response).onComplete(handler);
    }

    private static Future<MessageReader> createReader(
        MultiMap headers,
        Consumer<Handler<Throwable>> fail,
        Consumer<Handler<Buffer>> success) {

        Promise<MessageReader> prom = Promise.promise();

        fail.accept(prom::tryFail);
        success.accept(b -> {
            try {
                prom.complete(createReader(headers, b));
            } catch (IllegalArgumentException e) {
                prom.fail(e);
            }
        });
        return prom.future();

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
     * Creates a {@link MessageWriter} that can write both structured and binary messages to a {@link HttpClientRequest}.
     * When the writer finished to write the {@link MessageReader}, the request is ended with {@link HttpClientRequest#end(io.vertx.core.buffer.Buffer)}
     *
     * @param req the request to write
     * @return the message writer
     */
    public static MessageWriter<CloudEventWriter<HttpClientRequest>, HttpClientRequest> createWriter(HttpClientRequest req) {
        return new VertxHttpClientRequestMessageWriterImpl(req);
    }
}
