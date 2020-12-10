package io.cloudevents.http.vertx;

import io.cloudevents.core.message.MessageReader;
import io.cloudevents.core.message.MessageWriter;
import io.cloudevents.core.message.impl.GenericStructuredMessageReader;
import io.cloudevents.core.message.impl.MessageUtils;
import io.cloudevents.http.vertx.impl.BinaryVertxMessageReaderImpl;
import io.cloudevents.http.vertx.impl.CloudEventsHeaders;
import io.cloudevents.http.vertx.impl.VertxHttpServerResponseMessageWriterImpl;
import io.cloudevents.http.vertx.impl.VertxWebClientRequestMessageWriterImpl;
import io.cloudevents.lang.Nullable;
import io.cloudevents.rw.CloudEventRWException;
import io.cloudevents.rw.CloudEventWriter;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.HttpResponse;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * This class provides a collection of methods to create {@link io.cloudevents.core.message.MessageReader}
 * and {@link io.cloudevents.core.message.MessageWriter} for Vert.x {@link io.vertx.core.http.HttpServer} and {@link io.vertx.ext.web.client.WebClient}.
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
     * @return a {@link MessageReader} implementation
     * @throws CloudEventRWException if the encoding is unknown or something went wrong while parsing the headers
     */
    public static MessageReader createReader(MultiMap headers, @Nullable Buffer body) throws CloudEventRWException {
        return MessageUtils.parseStructuredOrBinaryMessage(
            () -> headers.get(HttpHeaders.CONTENT_TYPE),
            format -> {
                if (body != null) {
                    return new GenericStructuredMessageReader(format, body.getBytes());
                }
                throw CloudEventRWException.newOther(new IllegalStateException(
                    "Found a structured message using format " + format.serializedContentType() + " with null body"
                ));
            },
            () -> headers.get(CloudEventsHeaders.SPEC_VERSION),
            sv -> new BinaryVertxMessageReaderImpl(sv, headers, body)
        );
    }

    /**
     * Build a {@link MessageReader} starting from an {@link HttpServerRequest}.
     *
     * @param request the input request
     * @return a succeeded {@link Future} with the {@link MessageReader},
     * otherwise a failed {@link Future} if something went wrong while reading the body or while creating the {@link MessageReader}
     */
    public static Future<MessageReader> createReader(HttpServerRequest request) {
        return request
            .body()
            .map(b -> createReader(request.headers(), b));
    }

    /**
     * @see #createReader(HttpServerRequest)
     */
    public static void createReader(HttpServerRequest request, Handler<AsyncResult<MessageReader>> handler) {
        createReader(request).onComplete(handler);
    }

    /**
     * Build a {@link MessageReader} starting from an {@link HttpResponse}.
     *
     * @param response the input web client response
     * @return a {@link MessageReader} implementation
     * @throws CloudEventRWException if the encoding is unknown or something went wrong while parsing the headers
     */
    public static MessageReader createReader(HttpResponse<Buffer> response) throws CloudEventRWException {
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
