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
package io.cloudevents.spring.mvc;

import java.io.IOException;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.CloudEventUtils;
import io.cloudevents.spring.http.CloudEventHttpUtils;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.util.StreamUtils;

/**
 * An {@link HttpMessageConverter} for {@link CloudEvent CloudEvents}. Supports the use of
 * {@link CloudEvent} in a <code>&#64;RequestMapping</code> as either a method parameter
 * or a return value.
 * 
 * @author Dave Syer
 *
 */
public class CloudEventHttpMessageConverter extends AbstractHttpMessageConverter<CloudEvent> {

	public CloudEventHttpMessageConverter() {
		super(MediaType.APPLICATION_OCTET_STREAM, MediaType.ALL);
	}

	@Override
	protected boolean supports(Class<?> clazz) {
		return CloudEvent.class.isAssignableFrom(clazz);
	}

	@Override
	protected CloudEvent readInternal(Class<? extends CloudEvent> clazz, HttpInputMessage inputMessage)
			throws IOException, HttpMessageNotReadableException {
		byte[] body = StreamUtils.copyToByteArray(inputMessage.getBody());
		return CloudEventHttpUtils.toReader(inputMessage.getHeaders(), () -> body).toEvent();
	}

	@Override
	protected void writeInternal(CloudEvent event, HttpOutputMessage outputMessage)
			throws IOException, HttpMessageNotWritableException {
		CloudEventUtils.toReader(event)
				.read(CloudEventHttpUtils.toWriter(outputMessage.getHeaders(), body -> copy(body, outputMessage)));
	}

	private void copy(byte[] body, HttpOutputMessage outputMessage) {
		try {
			StreamUtils.copy(body, outputMessage.getBody());
		}
		catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

}
