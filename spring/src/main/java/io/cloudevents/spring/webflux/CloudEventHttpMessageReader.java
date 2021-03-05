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
import io.cloudevents.core.message.MessageReader;
import io.cloudevents.spring.http.CloudEventHttpUtils;
import org.springframework.core.ResolvableType;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ReactiveHttpInputMessage;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.util.StreamUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * A reactive {@link HttpMessageReader} for {@link CloudEvent CloudEvents}, converting
 * from an HTTP request to a CloudEvent. Supports the use of {@link CloudEvent} as an
 * input to a reactive endpoint.
 *
 * @author Dave Syer
 */
public class CloudEventHttpMessageReader implements HttpMessageReader<CloudEvent> {

	@Override
	public List<MediaType> getReadableMediaTypes() {
		return Arrays.asList(MediaType.APPLICATION_OCTET_STREAM, MediaType.ALL);
	}

	@Override
	public boolean canRead(ResolvableType elementType, MediaType mediaType) {
		return CloudEvent.class.isAssignableFrom(elementType.toClass());
	}

	@Override
	public Flux<CloudEvent> read(ResolvableType elementType, ReactiveHttpInputMessage message,
			Map<String, Object> hints) {
		return Flux.from(readMono(elementType, message, hints));
	}

	@Override
	public Mono<CloudEvent> readMono(ResolvableType elementType, ReactiveHttpInputMessage message,
			Map<String, Object> hints) {
		HttpHeaders headers = message.getHeaders();
		Mono<byte[]> body = DataBufferUtils.join(message.getBody()).map(buffer -> {
			try {
				return StreamUtils.copyToByteArray(buffer.asInputStream(true));
			}
			catch (Exception e) {
				throw new IllegalArgumentException(e);
			}
		});
		return body.map(bytes -> CloudEventHttpUtils.toReader(headers, () -> bytes)).map(MessageReader::toEvent);
	}

}
