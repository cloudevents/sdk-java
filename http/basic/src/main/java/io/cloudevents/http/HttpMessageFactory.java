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
 */

package io.cloudevents.http;

import io.cloudevents.core.message.MessageReader;
import io.cloudevents.core.message.MessageWriter;
import io.cloudevents.core.message.impl.GenericStructuredMessageReader;
import io.cloudevents.core.message.impl.MessageUtils;
import io.cloudevents.http.impl.CloudEventsHeaders;
import io.cloudevents.http.impl.HttpMessageReader;
import io.cloudevents.http.impl.HttpMessageWriter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static io.cloudevents.http.impl.CloudEventsHeaders.CONTENT_TYPE;

/**
 * This class provides a collection of methods to create {@link io.cloudevents.core.message.MessageReader}
 * and {@link io.cloudevents.core.message.MessageWriter} for various HTTP APIs.
 */
public final class HttpMessageFactory {

    private HttpMessageFactory() {}

    /**
     * Creates a new {@link MessageReader} that can read both structured and binary messages from a HTTP response (client) or request (server).
     *
     * <pre>
     * Example of usage with <a href="https://docs.oracle.com/javaee/6/api/javax/servlet/http/HttpServletRequest.html">HttpServletRequest</a>:
     * {@code
     * Consumer<BiConsumer<String,String>> forEachHeader = processHeader -> {
     *     Enumeration<String> headerNames = httpServletRequest.getHeaderNames();
     *     while (headerNames.hasMoreElements()) {
     *         String name = headerNames.nextElement();
     *         processHeader.accept(name, httpServletRequest.getHeader(name));
     *
     *     }
     * };
     * byte[] body = httpServletRequest.getInputStream().readAllBytes();
     * HttpMessageFactory.createReader(forEachHeader, body);
     * }
     * </pre>
     * @param forEachHeader http headers visitor function
     * @param body          nullable buffer of the body
     * @return a message reader implementation with potentially an unknown encoding
     * @throws IllegalArgumentException If, in case of binary mode, the spec version is invalid
     */
    public static MessageReader createReader(Consumer<BiConsumer<String, String>> forEachHeader, byte[] body) {
        final AtomicReference<String> contentType = new AtomicReference<>();
        final AtomicReference<String> specVersion = new AtomicReference<>();

        forEachHeader.accept((k, v) -> {
            if (CONTENT_TYPE.equalsIgnoreCase(k)) {
                contentType.set(v);
            } else if (CloudEventsHeaders.SPEC_VERSION.equalsIgnoreCase(k)) {
                specVersion.set(v);
            }
        });

        return MessageUtils.parseStructuredOrBinaryMessage(
            contentType::get,
            format -> new GenericStructuredMessageReader(format, body),
            specVersion::get,
            sv -> new HttpMessageReader(sv, forEachHeader, body)
        );
    }

    /**
     * Creates a new {@link MessageReader} that can read both structured and binary messages from a HTTP response (client) or request (server).
     *
     * <pre>
     * This overload is equivalent to calling:
     * {@code HttpMessageFactory.createReader(headers::forEach, body);}
     * </pre>
     * @param headers http headers as map
     * @param body    nullable buffer of the body
     * @return a message reader implementation with potentially an unknown encoding
     * @throws IllegalArgumentException If, in case of binary mode, the spec version is invalid
     */
    public static MessageReader createReader(Map<String, String> headers, byte[] body) {
        return createReader(headers::forEach, body);
    }

    /**
     * Creates a new {@link MessageReader} that can read both structured and binary messages from a HTTP response (client) or request (server).
     *
     * @param headers http headers as multimap
     * @param body    nullable buffer of the body
     * @return a message reader implementation with potentially an unknown encoding
     * @throws IllegalArgumentException If, in case of binary mode, the spec version is invalid
     */
    public static MessageReader createReaderFromMultimap(Map<String, List<String>> headers, byte[] body) {
        Consumer<BiConsumer<String, String>> forEachHeader = processHeader ->
            headers.forEach((key, values) ->
                values.forEach(value -> processHeader.accept(key, value)));
        return createReader(forEachHeader, body);
    }

    /**
     * Creates a new {@link MessageWriter} that can write both structured and binary messages to a HTTP response (server) or request (client).
     *
     * <pre>
     * Example of usage with <a href="https://docs.oracle.com/javaee/6/api/javax/servlet/http/HttpServletResponse.html">HttpServletResponse</a>:
     * {@code
     * HttpMessageFactory.createWriter(
     *     httpServletResponse::addHeader,
     *     body -> {
     *         try {
     *             if (body != null) {
     *                 httpServletResponse.setContentLength(body.length);
     *                 httpServletResponse.setStatus(HttpStatus.OK_200);
     *                 try (ServletOutputStream outputStream = httpServletResponse.getOutputStream()) {
     *                     outputStream.write(body);
     *                 }
     *             } else {
     *                 httpServletResponse.setStatus(HttpStatus.NO_CONTENT_204);
     *             }
     *         } catch (IOException e) {
     *             throw new UncheckedIOException(e);
     *         }
     * });
     * }
     * </pre>
     * @param putHeader a function that puts header into HTTP request or response.
     * @param sendBody  a function that sends body (e.g. sets HTTP status code, content-length and writes the bytes into output stream).
     * @return a message writer
     */
    public static HttpMessageWriter createWriter(BiConsumer<String, String> putHeader, Consumer<byte[]> sendBody) {
        return new HttpMessageWriter(putHeader, sendBody);
    }

}
