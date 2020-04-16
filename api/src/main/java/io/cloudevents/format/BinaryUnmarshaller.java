/**
 * Copyright 2019 The CloudEvents Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.cloudevents.format;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import io.cloudevents.Attributes;
import io.cloudevents.CloudEvent;
import io.cloudevents.extensions.ExtensionFormat;
import io.cloudevents.format.builder.HeadersStep;
import io.cloudevents.format.builder.PayloadStep;
import io.cloudevents.format.builder.UnmarshalStep;
import io.cloudevents.fun.AttributeUnmarshaller;
import io.cloudevents.fun.BinaryFormatAttributeMapper;
import io.cloudevents.fun.DataUnmarshaller;
import io.cloudevents.fun.EventBuilder;
import io.cloudevents.fun.ExtensionUmarshaller;
import io.cloudevents.fun.FormatExtensionMapper;

/**
 *
 * @author fabiojose
 *
 */
public final class BinaryUnmarshaller {
	private BinaryUnmarshaller() {}

	/**
	 * Gets a new builder instance
	 * @param <A> The attributes type
	 * @param <T> The 'data' type
	 * @param <P> The payload type
	 * @return
	 */
	public static <A extends Attributes, T, P> AttributeMapStep<A, T, P>
			builder() {
		return new Builder<A, T, P>();
	}

	public interface AttributeMapStep<A extends Attributes, T, P> {
		/**
		 * Maps the map of headers into map of attributes
		 * @param unmarshaller
		 * @return
		 */
		AttributeUmarshallStep<A, T, P> map(BinaryFormatAttributeMapper unmarshaller);
	}

	public interface AttributeUmarshallStep<A extends Attributes, T, P> {
		/**
		 * Unmarshals the map of attributes into instance of {@link Attributes}
		 * @param unmarshaller
		 * @return
		 */
		DataUnmarshallerStep<A, T, P> map(AttributeUnmarshaller<A> unmarshaller);
	}

	public interface DataUnmarshallerStep<A extends Attributes, T, P> {
		/**
		 * Unmarshals the payload into actual 'data' type
		 * @param unmarshaller
		 * @return
		 */
		DataUnmarshallerStep<A, T, P> map(String mime, DataUnmarshaller<P, T, A> unmarshaller);

		ExtensionsMapStep<A, T, P> next();
	}

	public interface ExtensionsMapStep<A extends Attributes, T, P> {
		/**
		 * Maps the headers map into map of extensions
		 * @param mapper
		 * @return
		 */
		ExtensionsStep<A, T, P> map(FormatExtensionMapper mapper);
	}

	public interface ExtensionsStepBegin<A extends Attributes, T, P> {
		/**
		 * Starts the configuration for extensions unmarshal
		 * @return
		 */
		ExtensionsStep<A, T, P> beginExtensions();
	}

	public interface ExtensionsStep<A extends Attributes, T, P> {
		/**
		 * Unmarshals a extension, based on the map of extensions.
		 *
		 * <br>
		 * <br>
		 * This is an optional step, because you do not have extensions or
		 * do not want to process them at all.
		 *
		 * @param unmarshaller
		 * @return
		 */
		ExtensionsStep<A, T, P> map(ExtensionUmarshaller unmarshaller);

		/**
		 * Ends the configuration for extensions unmarshal
		 * @return
		 */
		BuilderStep<A, T, P> next();
	}

	public interface BuilderStep<A extends Attributes, T, P> {
		/**
		 * Takes the builder to build {@link CloudEvent} instances
		 * @param builder
		 * @return
		 */
		HeadersStep<A, T, P> builder(EventBuilder<T, A> builder);
	}

	private static final class Builder<A extends Attributes, T, P> implements
		AttributeMapStep<A, T, P>,
		AttributeUmarshallStep<A, T, P>,
		DataUnmarshallerStep<A, T, P>,
		ExtensionsMapStep<A, T, P>,
		ExtensionsStep<A, T, P>,
		BuilderStep<A, T, P>,
		HeadersStep<A, T, P>,
		PayloadStep<A, T, P>,
		UnmarshalStep<A, T>{

		private BinaryFormatAttributeMapper attributeMapper;
		private AttributeUnmarshaller<A> attributeUnmarshaller;
		private Map<String, DataUnmarshaller<P, T, A>> dataUnmarshallers =
				new HashMap<>();
		private FormatExtensionMapper extensionMapper;
		private Set<ExtensionUmarshaller> extensionUnmarshallers =
				new HashSet<>();
		private EventBuilder<T, A> eventBuilder;
		private Supplier<Map<String, Object>> headersSupplier;
		private Supplier<P> payloadSupplier;

		@Override
		public AttributeUmarshallStep<A, T, P> map(BinaryFormatAttributeMapper mapper) {
			this.attributeMapper = mapper;
			return this;
		}

		@Override
		public DataUnmarshallerStep<A, T, P> map(AttributeUnmarshaller<A> unmarshaller) {
			this.attributeUnmarshaller = unmarshaller;
			return this;
		}

		@Override
		public DataUnmarshallerStep<A, T, P> map(String mime, DataUnmarshaller<P, T, A> unmarshaller) {
			this.dataUnmarshallers.put(mime, unmarshaller);
			return this;
		}

		public Builder<A, T, P> next() {
			return this;
		}

		@Override
		public ExtensionsStep<A, T, P> map(FormatExtensionMapper mapper) {
			this.extensionMapper = mapper;
			return this;
		}

		@Override
		public ExtensionsStep<A, T, P> map(ExtensionUmarshaller unmarshaller) {
			this.extensionUnmarshallers.add(unmarshaller);
			return this;
		}

		@Override
		public HeadersStep<A, T, P> builder(EventBuilder<T, A> builder) {
			this.eventBuilder = builder;
			return this;
		}

		@Override
		public PayloadStep<A, T, P> withHeaders(
				Supplier<Map<String, Object>> headers) {
			this.headersSupplier = headers;
			return this;
		}

		@Override
		public UnmarshalStep<A, T> withPayload(Supplier<P> payload) {
			this.payloadSupplier = payload;
			return this;
		}

		@Override
		public CloudEvent<A, T> unmarshal() {

			Map<String, Object> headers = headersSupplier.get();
			P payload = payloadSupplier.get();

			Map<String, String> attributesMap = attributeMapper.map(headers);

			A attributes = attributeUnmarshaller.unmarshal(attributesMap);

			T data = attributes.getDataContentType()
				.map((mime) -> dataUnmarshallers.get(mime))
				.filter(Objects::nonNull)
				.map(unmarshaller ->
						unmarshaller.unmarshal(payload, attributes))
				.orElse(null);

			final Map<String, String> extensionsMap =
					extensionMapper.map(headers);

			List<ExtensionFormat> extensions =
				extensionUnmarshallers.stream()
					.map(unmarshaller ->
						unmarshaller.unmarshal(extensionsMap))
					.filter(Optional::isPresent)
					.map(Optional::get)
					.collect(Collectors.toList());

			return eventBuilder.build(data, attributes, extensions);
		}
	}
}
