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
package io.cloudevents.spring.webflux;

import io.cloudevents.CloudEvent;
import io.cloudevents.CloudEventData;
import io.cloudevents.SpecVersion;
import io.cloudevents.core.CloudEventUtils;
import io.cloudevents.core.format.EventFormat;
import io.cloudevents.core.message.MessageWriter;
import io.cloudevents.rw.CloudEventContextWriter;
import io.cloudevents.rw.CloudEventRWException;
import io.cloudevents.rw.CloudEventWriter;
import io.cloudevents.spring.http.CloudEventsHeaders;
import org.reactivestreams.Publisher;
import org.springframework.core.ResolvableType;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ReactiveHttpOutputMessage;
import org.springframework.http.codec.HttpMessageWriter;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * A reactive {@link HttpMessageWriter} for {@link CloudEvent CloudEvents}, converting
 * from a CloudEvent to an HTTP response. Supports the use of {@link CloudEvent} as an
 * output from a reactive endpoint.
 *
 * @author Dave Syer
 */
public class CloudEventHttpMessageWriter implements HttpMessageWriter<CloudEvent> {

	@Override
	public List<MediaType> getWritableMediaTypes() {
		return Arrays.asList(MediaType.APPLICATION_OCTET_STREAM, MediaType.ALL);
	}

	@Override
	public boolean canWrite(ResolvableType elementType, MediaType mediaType) {
		return CloudEvent.class.isAssignableFrom(elementType.toClass());
	}

	@Override
	public Mono<Void> write(Publisher<? extends CloudEvent> inputStream, ResolvableType elementType,
			MediaType mediaType, ReactiveHttpOutputMessage message, Map<String, Object> hints) {
		return Mono.from(inputStream).map(CloudEventUtils::toReader)
				.flatMap(reader -> reader.read(new ReactiveHttpMessageWriter(message)));
	}

	private static class ReactiveHttpMessageWriter
			implements MessageWriter<CloudEventWriter<Mono<Void>>, Mono<Void>>, CloudEventWriter<Mono<Void>> {

		private final ReactiveHttpOutputMessage response;

		public ReactiveHttpMessageWriter(ReactiveHttpOutputMessage response) {
			this.response = response;
		}

        // Binary visitor factory

        @Override
        public CloudEventWriter<Mono<Void>> create(SpecVersion version) {
            this.response.getHeaders().set(CloudEventsHeaders.SPEC_VERSION, version.toString());
            return this;
        }

        // Binary visitor

        @Override
        public CloudEventContextWriter withContextAttribute(String name, String value) throws CloudEventRWException {
            String headerName = CloudEventsHeaders.ATTRIBUTES_TO_HEADERS.get(name);
            if (headerName == null) {
                headerName = "ce-" + name;
            }
            this.response.getHeaders().set(headerName, value);
            return this;
        }

        @Override
        public Mono<Void> end(CloudEventData value) throws CloudEventRWException {
            return copy(value.toBytes(), this.response);
        }

        @Override
        public Mono<Void> end() {
            return copy(new byte[0], this.response);
        }

		// Structured visitor

		@Override
		public Mono<Void> setEvent(EventFormat format, byte[] value) throws CloudEventRWException {
			this.response.getHeaders().set(HttpHeaders.CONTENT_TYPE, format.serializedContentType());
			return copy(value, this.response);
		}

		private Mono<Void> copy(byte[] bytes, ReactiveHttpOutputMessage message) {
			DataBuffer data = message.bufferFactory().wrap(bytes);
			message.getHeaders().setContentLength(bytes.length);
			return message.writeWith(Mono.just(data));
		}
    }

}
