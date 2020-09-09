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

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.util.Enumeration;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.message.MessageReader;
import io.cloudevents.core.message.MessageWriter;
import io.cloudevents.http.HttpMessageFactory;

import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

public class JettyServer {

    private static class Handler extends AbstractHandler {

        @Override
        public void handle(String uri,
                           Request request,
                           HttpServletRequest httpServletRequest,
                           HttpServletResponse httpServletResponse) throws IOException, ServletException {
            if (!"/echo".equalsIgnoreCase(uri)) {
                httpServletResponse.setStatus(HttpStatus.NOT_FOUND_404);
                return;
            }
            if (!"POST".equalsIgnoreCase(request.getMethod())) {
                httpServletResponse.setStatus(HttpStatus.METHOD_NOT_ALLOWED_405);
                return;
            }

            CloudEvent receivedEvent = createMessageReader(httpServletRequest).toEvent();
            System.out.println("Handling event: " + receivedEvent);
            createMessageWriter(httpServletResponse).writeBinary(receivedEvent);
        }
    }

    private static MessageReader createMessageReader(HttpServletRequest httpServletRequest) throws IOException {
        Consumer<BiConsumer<String, String>> forEachHeader = processHeader -> {
            Enumeration<String> headerNames = httpServletRequest.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String name = headerNames.nextElement();
                processHeader.accept(name, httpServletRequest.getHeader(name));

            }
        };
        byte[] body = IOUtils.toByteArray(httpServletRequest.getInputStream());
        return HttpMessageFactory.createReader(forEachHeader, body);
    }

    private static MessageWriter createMessageWriter(HttpServletResponse httpServletResponse) throws IOException {
        return HttpMessageFactory.createWriter(
            httpServletResponse::addHeader,
            body -> {
                try {
                    try (ServletOutputStream outputStream = httpServletResponse.getOutputStream()) {
                        if (body != null) {
                            httpServletResponse.setContentLength(body.length);
                            httpServletResponse.setStatus(HttpStatus.OK_200);
                            outputStream.write(body);
                        } else {
                            httpServletResponse.setStatus(HttpStatus.NO_CONTENT_204);
                        }
                    }
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            });
    }

    public static void main(String[] args) throws Exception {
        Server server = new Server(new InetSocketAddress("localhost", 8080));
        server.setHandler(new Handler());
        server.start();
        server.join();
    }
}
