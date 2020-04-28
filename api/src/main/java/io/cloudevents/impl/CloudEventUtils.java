/**
 * Copyright 2020 The CloudEvents Authors
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.cloudevents.impl;

import java.util.Map;

import io.cloudevents.Attributes;
import io.cloudevents.CloudEvent;
import io.cloudevents.format.EventFormat;
import io.cloudevents.message.BinaryMessage;
import io.cloudevents.message.BinaryMessageAttributes;
import io.cloudevents.message.BinaryMessageExtensionsVisitor;
import io.cloudevents.message.BinaryMessageVisitor;
import io.cloudevents.message.BinaryMessageVisitorFactory;
import io.cloudevents.message.MessageVisitException;
import io.cloudevents.message.StructuredMessage;
import io.cloudevents.message.StructuredMessageVisitor;
import io.cloudevents.v1.AttributesImpl;

/**
 * Set of miscellaneous utility methods to manipulate various aspects of {@link CloudEvent}.
 *
 * Mainly for internal use within the framework
 */
public abstract class CloudEventUtils {

	public static io.cloudevents.v1.CloudEventBuilder buildV1() {
		return new io.cloudevents.v1.CloudEventBuilder();
	}

	public static io.cloudevents.v1.CloudEventBuilder buildV1(CloudEvent event) {
		return new io.cloudevents.v1.CloudEventBuilder(event);
	}

	public static io.cloudevents.v03.CloudEventBuilder buildV03() {
		return new io.cloudevents.v03.CloudEventBuilder();
	}

	public static io.cloudevents.v03.CloudEventBuilder buildV03(CloudEvent event) {
		return new io.cloudevents.v03.CloudEventBuilder(event);
	}

	public static CloudEvent toV03(CloudEvent cloudEvent) {
		return new CloudEventImpl(extractAttributes(cloudEvent).toV03(), cloudEvent.getData(),
				cloudEvent.getExtensions());
	}

	public static CloudEvent toV1(CloudEvent cloudEvent) {
		return new CloudEventImpl(extractAttributes(cloudEvent).toV1(), cloudEvent.getData(),
				cloudEvent.getExtensions());
	}

	public static Attributes extractAttributes(CloudEvent cloudEvent) {
		if (cloudEvent instanceof CloudEventImpl) {
			return ((CloudEventImpl) cloudEvent).getAttributes();
		} else {
			return new AttributesImpl(cloudEvent.getId(), cloudEvent.getSource(), cloudEvent.getType(),
					cloudEvent.getDataContentType(), cloudEvent.getDataSchema(), cloudEvent.getSubject(),
					cloudEvent.getTime());
		}
	}

	public static BinaryMessage asBinaryMessage(CloudEvent cloudEvent) {
		return new BinaryMessage() {
			@Override
			public <V extends BinaryMessageVisitor<R>, R> R visit(BinaryMessageVisitorFactory<V, R> visitorFactory)
					throws MessageVisitException, IllegalStateException {
				return CloudEventUtils.visit(cloudEvent, visitorFactory);
			}
		};
	}

	public static StructuredMessage asStructuredMessage(CloudEvent cloudEvent, EventFormat format) {
		// TODO This sucks, will improve later
		return new StructuredMessage() {
			@Override
			public <T> T visit(StructuredMessageVisitor<T> visitor)
					throws MessageVisitException, IllegalStateException {
				return visitor.setEvent(format, format.serialize(cloudEvent));
			}
		};
	}

	public static void visitExtensions(CloudEvent cloudEvent, BinaryMessageExtensionsVisitor visitor) throws MessageVisitException {
		// TODO to be improved
		for (Map.Entry<String, Object> entry : cloudEvent.getExtensions().entrySet()) {
			if (entry.getValue() instanceof String) {
				visitor.setExtension(entry.getKey(), (String) entry.getValue());
			} else if (entry.getValue() instanceof Number) {
				visitor.setExtension(entry.getKey(), (Number) entry.getValue());
			} else if (entry.getValue() instanceof Boolean) {
				visitor.setExtension(entry.getKey(), (Boolean) entry.getValue());
			} else {
				// This should never happen because we build that map only through our builders
				throw new IllegalStateException("Illegal value inside extensions map: " + entry);
			}
		}
	}

	public static <T extends BinaryMessageVisitor<V>, V> V visit(CloudEvent cloudEvent, BinaryMessageVisitorFactory<T, V> visitorFactory)
			throws MessageVisitException, IllegalStateException {
		Attributes attributes = extractAttributes(cloudEvent);
		BinaryMessageVisitor<V> visitor = visitorFactory.createBinaryMessageVisitor(extractAttributes(cloudEvent).getSpecVersion());
		if (attributes instanceof BinaryMessageAttributes) {
			((BinaryMessageAttributes)attributes).visitAttributes(visitor);
			visitExtensions(cloudEvent, visitor);
		}

		if (cloudEvent.getData() != null) {
			visitor.setBody(cloudEvent.getData());
		}

		return visitor.end();
	}

	@SuppressWarnings("rawtypes")
	public static BinaryMessageVisitorFactory defaultBinaryMessageVisitorFactory() {
		return specVersion -> {
			switch (specVersion) {
			case V1:
				return CloudEventUtils.buildV1();
			case V03:
				return CloudEventUtils.buildV03();
			default:
				throw new IllegalStateException("Unrecognized spec version: " + specVersion);
			}
		};
	}
}
