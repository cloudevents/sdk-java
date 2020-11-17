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
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

import org.springframework.core.ResolvableType;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.http.ReactiveHttpOutputMessage;
import org.springframework.http.codec.HttpMessageWriter;

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
		return Mono.from(inputStream).flatMap(event -> {
			message.getHeaders().addAll(CloudEventHttpUtils.toHttp(event));
			byte[] bytes = event.getData().toBytes();
			DataBuffer data = message.bufferFactory().wrap(bytes);
			message.getHeaders().setContentLength(bytes.length);
			return message.writeWith(Mono.just(data));
		});
	}

}