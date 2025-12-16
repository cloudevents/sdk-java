/*
 * Copyright 2020-Present The CloudEvents Authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.cloudevents.spring.http;

import io.cloudevents.CloudEvent;
import io.cloudevents.CloudEventContext;
import io.cloudevents.SpecVersion;
import io.cloudevents.core.CloudEventUtils;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.core.message.MessageReader;
import io.cloudevents.http.HttpMessageFactory;
import io.cloudevents.http.impl.HttpMessageWriter;
import io.cloudevents.rw.CloudEventRWException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Miscellaneous utility methods to assist with Cloud Events in the context of Spring Web
 * frameworks. Primarily intended for the internal use within Spring-based frameworks or
 * integrations.
 *
 * @author Dave Syer
 * @since 2.0
 */
public class CloudEventHttpUtils {

	private CloudEventHttpUtils() {
	}

    /**
     * Create a {@link MessageReader} to convert an HTTP request to a {@link CloudEvent}.
     *
     * @param headers the HTTP request headers
     * @param body    the HTTP request body as a byte array
     * @return a {@link MessageReader} representing the {@link CloudEvent}
     * @throws CloudEventRWException if something goes wrong while resolving the {@link SpecVersion} or if the message has unknown encoding
     */
    public static MessageReader toReader(HttpHeaders headers, Supplier<byte[]> body) throws CloudEventRWException {
        Map<String, List<String>> headersMap = new HashMap<>();
        headers.forEach((key, values) -> headersMap.put(key, new ArrayList<>(values)));
        return HttpMessageFactory.createReaderFromMultimap(headersMap, body.get()); // TODO - Take header iterator instead
    }

	/**
	 * Create an {@link HttpMessageWriter} that can hand off a {@link CloudEvent} to an
	 * HTTP response. Mainly useful in a blocking (not async) setting because the response
	 * body has to be consumed directly.
     *
	 * @param headers the response headers (will be mutated)
	 * @param sendBody a consumer for the response body that puts the bytes on the wire
	 */
	public static HttpMessageWriter toWriter(HttpHeaders headers, Consumer<byte[]> sendBody) {
		return HttpMessageFactory.createWriter(headers::set, sendBody);
    }

    /**
     * Helper method for extracting {@link HttpHeaders} from a {@link CloudEvent}. Can,
     * for instance, be used in a {@link org.springframework.web.bind.annotation.RequestMapping} to return a
     * {@link ResponseEntity} that has headers copied from a {@link CloudEvent}.
     *
     * @param event the input {@link CloudEvent}
     * @return the response headers represented by the event
     * @throws CloudEventRWException if something goes wrong while writing the context to the http headers
     */
    public static HttpHeaders toHttp(CloudEventContext event) throws CloudEventRWException {
        HttpHeaders headers = new HttpHeaders();
        CloudEventUtils.toReader(CloudEventBuilder.fromContext(event).build()).read(toWriter(headers, bytes -> {
        }));
        return headers;
    }

    /**
     * Helper method for converting {@link HttpHeaders} to a {@link CloudEvent}. The input
     * headers <b>must</b> represent a valid event in "binary" form, i.e. it must have headers
     * {@code ce-id}, {@code ce-specversion} etc.
     *
     * @param headers the input request headers
     * @return a {@link CloudEventBuilder} that can be used to create a new {@link CloudEvent}
     * @throws CloudEventRWException if something goes wrong while reading the context from the http headers
     */
    public static CloudEventBuilder fromHttp(HttpHeaders headers) throws CloudEventRWException {
        return CloudEventBuilder
            .fromContext(CloudEventUtils.toEvent(CloudEventHttpUtils.toReader(headers, () -> null)));
	}

}
