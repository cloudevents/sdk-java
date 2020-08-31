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

import io.cloudevents.CloudEvent;
import io.cloudevents.core.message.MessageReader;
import io.cloudevents.core.message.MessageWriter;
import io.cloudevents.core.v1.CloudEventBuilder;
import io.cloudevents.http.HttpMessageFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class HttpURLConnectionClient {

    public static void main(String[] args) throws IOException {
        CloudEvent ceToSend = new CloudEventBuilder()
            .withId("my-id")
            .withSource(URI.create("/myClient"))
            .withType("dev.knative.cronjob.event")
            .withDataContentType("application/json")
            .withData("{ \"msg\" : \"hello\" }".getBytes(StandardCharsets.UTF_8))
            .build();


        URL url = new URL("http://localhost:8080/echo");
        HttpURLConnection httpUrlConnection = (HttpURLConnection) url.openConnection();
        httpUrlConnection.setRequestMethod("POST");
        httpUrlConnection.setDoOutput(true);
        httpUrlConnection.setDoInput(true);

        MessageWriter messageWriter = createMessageWriter(httpUrlConnection);
        messageWriter.writeBinary(ceToSend);

        MessageReader messageReader = createMessageReader(httpUrlConnection);
        CloudEvent receivedCE = messageReader.toEvent();

        System.out.println("CloudEvent: " + receivedCE);
        System.out.println("Data: " + new String(receivedCE.getData(), StandardCharsets.UTF_8));
    }

    private static MessageReader createMessageReader(HttpURLConnection httpUrlConnection) throws IOException {
        Map<String, List<String>> headers = httpUrlConnection.getHeaderFields();
        byte[] body = IOUtils.toByteArray(httpUrlConnection.getInputStream());
        return HttpMessageFactory.createReaderFromMultimap(headers, body);
    }

    private static MessageWriter createMessageWriter(HttpURLConnection httpUrlConnection) {
        return HttpMessageFactory.createWriter(
            httpUrlConnection::setRequestProperty,
            body -> {
                try {
                    if (body != null) {
                        httpUrlConnection.setRequestProperty("content-length", String.valueOf(body.length));
                        try (OutputStream outputStream = httpUrlConnection.getOutputStream()) {
                            outputStream.write(body);
                        }
                    } else {
                        httpUrlConnection.setRequestProperty("content-length", "0");
                    }
                } catch (IOException t) {
                    throw new UncheckedIOException(t);
                }
            });
    }
}
