/*
 * Copyright 2002-2019 the original author or authors.
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

import io.cloudevents.CloudEvent;
import io.cloudevents.core.format.EventFormat;
import io.cloudevents.core.provider.EventFormatProvider;

import org.springframework.core.ResolvableType;
import org.springframework.core.codec.AbstractDataBufferDecoder;
import org.springframework.core.codec.DecodingException;
import org.springframework.core.codec.Hints;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;

/**
 * Encoder for {@link CloudEvent CloudEvents}.
 *
 * @author Arjen Poutsma
 * @since 5.0
 */
public class CloudEventDecoder extends AbstractDataBufferDecoder<CloudEvent> {

	public CloudEventDecoder() {
		super(MimeTypeUtils.ALL);
	}

	@Override
	public boolean canDecode(ResolvableType elementType, @Nullable MimeType mimeType) {
		Class<?> clazz = elementType.toClass();
		return super.canDecode(elementType, mimeType)
				&& CloudEvent.class.isAssignableFrom(clazz) && EventFormatProvider
						.getInstance().resolveFormat(mimeType.toString()) != null;
	}

	@Override
	public CloudEvent decode(DataBuffer buffer, ResolvableType targetType,
			MimeType mimeType, Map<String, Object> hints) throws DecodingException {
		if (logger.isDebugEnabled() && !Hints.isLoggingSuppressed(hints)) {
			String logPrefix = Hints.getLogPrefix(hints);
			logger.debug(logPrefix + "Reading CloudEvent");
		}
		EventFormat format = EventFormatProvider.getInstance()
				.resolveFormat(mimeType.toString());
		byte[] result = new byte[buffer.readableByteCount()];
		buffer.read(result);
		DataBufferUtils.release(buffer);
		return format.deserialize(result);
	}

}
