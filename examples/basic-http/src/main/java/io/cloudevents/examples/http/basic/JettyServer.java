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

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.message.MessageReader;
import io.cloudevents.core.message.MessageWriter;
import io.cloudevents.http.HttpMessageFactory;

import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.Callback;

public class JettyServer {

    private static class CloudeventsHandler extends Handler.Abstract {

        @Override
        public boolean handle(Request request, Response response, Callback callback) {
            if (!"/echo".equalsIgnoreCase(request.getHttpURI().getPath())) {
                return false;
            }
            if (!"POST".equalsIgnoreCase(request.getMethod())) {
                response.setStatus(HttpStatus.METHOD_NOT_ALLOWED_405);
                callback.succeeded();
                return true;
            }

            try {
                CloudEvent receivedEvent = createMessageReader(request).toEvent();
                System.out.println("Handling event: " + receivedEvent);
                createMessageWriter(response, callback).writeBinary(receivedEvent);
            } catch (Exception e) {
                response.setStatus(HttpStatus.BAD_REQUEST_400);
                callback.succeeded();
            }
            return true;
        }
    }

    private static MessageReader createMessageReader(Request request) {
        Consumer<BiConsumer<String, String>> forEachHeader = processHeader ->
            request.getHeaders()
                .forEach(header -> processHeader.accept(header.getName(), header.getValue()));
        byte[] body = request.read().takeByteArray();
        return HttpMessageFactory.createReader(forEachHeader, body);
    }

    private static MessageWriter createMessageWriter(Response response, Callback callback) {
        return HttpMessageFactory.createWriter(
            response.getHeaders()::add,
            body -> {
                if (body != null) {
                    response.setStatus(HttpStatus.OK_200);
                    response.write(true, ByteBuffer.wrap(body), callback);
                } else {
                    response.setStatus(HttpStatus.NO_CONTENT_204);
                    callback.succeeded();
                }
            });
    }

    public static void main(String[] args) throws Exception {
        Server server = new Server(new InetSocketAddress("localhost", 8080));
        server.setHandler(new CloudeventsHandler());
        server.start();
        server.join();
    }
}
