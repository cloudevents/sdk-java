/*
 * Copyright 2018-Present The CloudEvents Authors
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package io.cloudevents.http.vertx;

import io.cloudevents.core.message.MessageReader;
import io.cloudevents.core.message.impl.GenericStructuredMessageReader;
import io.cloudevents.core.message.impl.MessageUtils;
import io.cloudevents.core.message.impl.UnknownEncodingMessageReader;
import io.cloudevents.http.vertx.impl.BinaryVertxMessageReaderImpl;
import io.cloudevents.http.vertx.impl.CloudEventsHeaders;
import io.vertx.core.*;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerRequest;

/**
 * Interface representing a Message implementation using Vert.x types
 */
public interface VertxMessageReaderFactory {

    /**
     * Create a new Message using Vert.x headers and body.
     *
     * @param headers Http headers
     * @param body    nullable buffer of the body
     * @return a Message implementation with potentially an unknown encoding
     * @throws IllegalArgumentException If, in case of binary mode, the spec version is invalid
     */
    static MessageReader create(MultiMap headers, Buffer body) throws IllegalArgumentException {
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
    static Future<MessageReader> fromHttpServerRequest(HttpServerRequest request) {
        Promise<MessageReader> prom = Promise.promise();

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
     * Like {@link VertxMessageReaderFactory#fromHttpServerRequest(HttpServerRequest)}
     *
     * @param request
     * @param handler
     */
    static void fromHttpServerRequest(HttpServerRequest request, Handler<AsyncResult<MessageReader>> handler) {
        fromHttpServerRequest(request).onComplete(handler);
    }

    /**
     * Build a message starting from an {@link HttpClientResponse}
     *
     * @param request
     * @return
     */
    static Future<MessageReader> fromHttpClientResponse(HttpClientResponse request) {
        Promise<MessageReader> prom = Promise.promise();

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
     * Like {@link VertxMessageReaderFactory#fromHttpClientResponse(HttpClientResponse)}
     *
     * @param response
     * @param handler
     */
    static void fromHttpClientResponse(HttpClientResponse response, Handler<AsyncResult<MessageReader>> handler) {
        fromHttpClientResponse(response).onComplete(handler);
    }

}
