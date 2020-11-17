/*
 * Copyright 2019-2019 the original author or authors.
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

import java.util.function.Consumer;
import java.util.function.Supplier;

import io.cloudevents.CloudEvent;
import io.cloudevents.CloudEventContext;
import io.cloudevents.core.CloudEventUtils;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.core.message.MessageReader;
import io.cloudevents.http.HttpMessageFactory;
import io.cloudevents.http.impl.HttpMessageWriter;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

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
	 * Create a {@link MessageReader} to assist in conversion of an HTTP request to a
	 * {@link CloudEvent}.
	 * @param headers the HTTP request headers
	 * @param body the HTTP request body as a byte array
	 * @return a {@link MessageReader} representing the {@link CloudEvent}
	 */
	public static MessageReader toReader(HttpHeaders headers, Supplier<byte[]> body) {
		return HttpMessageFactory.createReaderFromMultimap(headers, body.get());
	}

	/**
	 * Create an {@link HttpMessageWriter} that can hand off a {@link CloudEvent} to an
	 * HTTP response. Mainly useful in a blocking (not async) setting because the response
	 * body has to be consumed directly.
	 * @param headers the response headers (will be mutated)
	 * @param sendBody a consumer for the response body that puts the bytes on the wire
	 */
	public static HttpMessageWriter toWriter(HttpHeaders headers, Consumer<byte[]> sendBody) {
		return HttpMessageFactory.createWriter(headers::set, sendBody);
	}

	/**
	 * Helper method for extracting {@link HttpHeaders} from a {@link CloudEvent}. Can,
	 * for instance, be used in a <code>&#64;RequestMapping</code> to return a
	 * {@link ResponseEntity} that has headers copied from a {@link CloudEvent}.
	 * @param event the input {@link CloudEvent}
	 * @return the response headers represented by the event
	 */
	public static HttpHeaders toHttp(CloudEventContext event) {
		HttpHeaders headers = new HttpHeaders();
		CloudEventUtils.toReader(CloudEventBuilder.fromContext(event).build()).read(toWriter(headers, bytes -> {
		}));
		return headers;
	}

	/**
	 * Helper method for converting {@link HttpHeaders} to a {@link CloudEvent}. The input
	 * headers must represent a valid event in "binary" form, i.e. it must have headers
	 * "ce-id", "ce-specversion" etc.
	 * @param headers the input request headers
	 * @return a {@link CloudEventBuilder} that can be used to create a new
	 * {@link CloudEvent}
	 * 
	 */
	public static CloudEventBuilder fromHttp(HttpHeaders headers) {
		return CloudEventBuilder
				.fromContext(CloudEventUtils.toEvent(CloudEventHttpUtils.toReader(headers, () -> null)));
	}

}
