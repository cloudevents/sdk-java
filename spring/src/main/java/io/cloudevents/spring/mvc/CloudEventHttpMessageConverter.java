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
import io.cloudevents.spring.http.CloudEventHttpUtils;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.util.StreamUtils;

/**
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
		return CloudEventHttpUtils.fromHttp(inputMessage.getHeaders())
				.withData(StreamUtils.copyToByteArray(inputMessage.getBody())).build();
	}

	@Override
	protected void writeInternal(CloudEvent event, HttpOutputMessage outputMessage)
			throws IOException, HttpMessageNotWritableException {
		outputMessage.getHeaders().addAll(CloudEventHttpUtils.toHttp(event));
		StreamUtils.copy(event.getData().toBytes(), outputMessage.getBody());
	}

}
