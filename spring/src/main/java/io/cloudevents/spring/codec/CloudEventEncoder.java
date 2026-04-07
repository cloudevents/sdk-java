/*
 * Copyright 2021-Present The CloudEvents Authors.
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

package io.cloudevents.spring.codec;

import java.util.Map;
import java.util.stream.Collectors;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.format.EventFormat;
import io.cloudevents.core.provider.EventFormatProvider;

import org.jspecify.annotations.Nullable;
import reactor.core.publisher.Flux;

import org.springframework.core.ResolvableType;
import org.springframework.core.codec.AbstractSingleValueEncoder;
import org.springframework.core.codec.Hints;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;

/**
 * Encoder for {@link CloudEvent CloudEvents}.
 *
 * @author Dave Syer
 */
public class CloudEventEncoder extends AbstractSingleValueEncoder<CloudEvent> {

	public CloudEventEncoder() {
		super(EventFormatProvider.getInstance().getContentTypes().stream()
				.map(type -> MimeTypeUtils.parseMimeType(type))
				.collect(Collectors.toList()).toArray(new MimeType[0]));
	}

	@Override
	public boolean canEncode(ResolvableType elementType, @Nullable MimeType mimeType) {
		Class<?> clazz = elementType.toClass();
		return super.canEncode(elementType, mimeType)
				&& CloudEvent.class.isAssignableFrom(clazz) && EventFormatProvider
						.getInstance().resolveFormat(mimeType.toString()) != null;
	}

	@Override
	protected Flux<DataBuffer> encode(CloudEvent event, DataBufferFactory bufferFactory,
			ResolvableType type, @Nullable MimeType mimeType,
			@Nullable Map<String, Object> hints) {
		return Flux.just(encodeValue(event, bufferFactory, type, mimeType, hints));
	}

	@Override
	public DataBuffer encodeValue(CloudEvent event, DataBufferFactory bufferFactory,
			ResolvableType valueType, MimeType mimeType, Map<String, Object> hints) {
		if (logger.isDebugEnabled() && !Hints.isLoggingSuppressed(hints)) {
			String logPrefix = Hints.getLogPrefix(hints);
			logger.debug(logPrefix + "Writing [" + event + "]");
		}
		EventFormat format = EventFormatProvider.getInstance()
				.resolveFormat(mimeType.toString());
		return bufferFactory.wrap(format.serialize(event));
	}

}
