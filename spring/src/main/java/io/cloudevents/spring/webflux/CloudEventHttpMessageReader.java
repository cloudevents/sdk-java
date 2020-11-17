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
package io.cloudevents.spring.webflux;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import io.cloudevents.CloudEvent;
import io.cloudevents.spring.http.CloudEventHttpUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.core.ResolvableType;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ReactiveHttpInputMessage;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.util.StreamUtils;

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
		return DataBufferUtils.join(message.getBody()).map(body -> {
			try {
				return CloudEventHttpUtils.fromHttp(headers)
						.withData(StreamUtils.copyToByteArray(body.asInputStream(true))).build();
			}
			catch (Exception e) {
				throw new IllegalArgumentException(e);
			}
		});
	}

}