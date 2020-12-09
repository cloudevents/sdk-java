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
package io.cloudevents.spring.messaging;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import io.cloudevents.CloudEventData;
import io.cloudevents.SpecVersion;
import io.cloudevents.core.format.EventFormat;
import io.cloudevents.core.message.MessageWriter;
import io.cloudevents.rw.CloudEventContextWriter;
import io.cloudevents.rw.CloudEventRWException;
import io.cloudevents.rw.CloudEventWriter;

/**
 * Internal utility class for copying <code>CloudEvent</code> context to a map (message
 * headers).
 * 
 * @author Dave Syer
 *
 */
class SpringMessageWriter implements CloudEventWriter<Void>, MessageWriter<SpringMessageWriter, Void> {

	private final BiConsumer<String, String> putHeader;

	private final Consumer<byte[]> putBody;

	public SpringMessageWriter(BiConsumer<String, String> putHeader) {
		this(putHeader, body -> {
		});
	}

	public SpringMessageWriter(BiConsumer<String, String> putHeader, Consumer<byte[]> putBody) {
		this.putHeader = putHeader;
		this.putBody = putBody;
	}

	@Override
	public Void setEvent(EventFormat format, byte[] value) throws CloudEventRWException {
		putHeader.accept(CloudEventsHeaders.CONTENT_TYPE, format.serializedContentType());
		putBody.accept(value);
		return null;
	}

	@Override
	public Void end(CloudEventData value) throws CloudEventRWException {
		putBody.accept(value.toBytes());
		return null;
	}

	@Override
	public Void end() {
		return null;
	}

	@Override
	public CloudEventContextWriter withContextAttribute(String name, String value) throws CloudEventRWException {
		putHeader.accept(CloudEventsHeaders.CE_PREFIX + name, value);
		return this;
	}

	@Override
	public SpringMessageWriter create(SpecVersion version) {
		putHeader.accept(CloudEventsHeaders.SPEC_VERSION, version.toString());
		return this;
	}

}