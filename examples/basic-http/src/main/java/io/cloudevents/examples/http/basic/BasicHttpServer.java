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

package io.cloudevents.examples.http.basic;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import io.cloudevents.CloudEvent;
import io.cloudevents.core.message.MessageReader;
import io.cloudevents.core.message.MessageWriter;
import io.cloudevents.http.HttpMessageFactory;

import java.io.*;
import java.net.InetSocketAddress;

public class BasicHttpServer {

    public static void main(String[] args) throws IOException {
        HttpServer httpServer = HttpServer.create(new InetSocketAddress("localhost", 8080), 0);
        httpServer.createContext("/echo", BasicHttpServer::echoHandler);
        httpServer.start();
    }

    private static void echoHandler(HttpExchange exchange) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, 0);
            return;
        }
        try {
            MessageReader messageReader = createMessageReader(exchange);
            CloudEvent cloudEvent = messageReader.toEvent();

            System.out.println("Handling event: " + cloudEvent);

            MessageWriter messageWriter = createMessageWriter(exchange);
            messageWriter.writeBinary(cloudEvent);
        } catch (Throwable t) {
            try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
                try (PrintWriter pw = new PrintWriter(byteArrayOutputStream)) {
                    t.printStackTrace(pw);
                }
                byte[] body = byteArrayOutputStream.toByteArray();
                exchange.sendResponseHeaders(500, body.length);
                try (OutputStream outputStream = exchange.getResponseBody()) {
                    outputStream.write(body);
                }
            }
        }
    }

    private static MessageReader createMessageReader(HttpExchange httpExchange) throws IOException {
        Headers headers = httpExchange.getRequestHeaders();
        byte[] body = IOUtils.toByteArray(httpExchange.getRequestBody());
        return HttpMessageFactory.createReaderFromMultimap(headers, body);
    }

    private static MessageWriter createMessageWriter(HttpExchange httpExchange) {
        return HttpMessageFactory.createWriter(
            httpExchange.getResponseHeaders()::add,
            body -> {
                try {
                    try (OutputStream os = httpExchange.getResponseBody()){
                        if (body != null) {
                            httpExchange.sendResponseHeaders(200, body.length);
                            os.write(body);
                        } else {
                            httpExchange.sendResponseHeaders(204, -1);
                        }
                    }
                } catch (IOException t) {
                    throw new UncheckedIOException(t);
                }
            }
        );
    }
}
