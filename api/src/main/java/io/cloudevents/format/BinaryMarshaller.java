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

import java.util.Collection;
import java.util.Map;
import java.util.function.Supplier;

import io.cloudevents.Attributes;
import io.cloudevents.CloudEvent;
import io.cloudevents.extensions.ExtensionFormat;
import io.cloudevents.format.builder.EventStep;
import io.cloudevents.format.builder.MarshalStep;
import io.cloudevents.fun.AttributeMarshaller;
import io.cloudevents.fun.DataMarshaller;
import io.cloudevents.fun.ExtensionFormatAccessor;
import io.cloudevents.fun.ExtensionMarshaller;
import io.cloudevents.fun.FormatHeaderMapper;
import io.cloudevents.fun.WireBuilder;

/**
 * 
 * @author fabiojose
 *
 */
public final class BinaryMarshaller {
	private BinaryMarshaller() {}

	/**
	 * Gets a new builder instance
	 * @param <A> The attributes type
	 * @param <T> The 'data' type
	 * @param <P> The payload type
	 * @return
	 */
	public static <A extends Attributes, T, P> 
		AttributeMarshalStep<A, T, P> builder() {
		
		return new Builder<A, T, P>();
	}
	
	public static interface AttributeMarshalStep<A extends Attributes, T, P> {
		/**
		 * Marshals the {@link Attributes} instance into a 
		 * {@code Map<String, String>}
		 * @param marshaller
		 * @return
		 */
		ExtensionsAccessorStep<A, T, P> map(AttributeMarshaller<A> marshaller);
	}
	
	public static interface ExtensionsAccessorStep<A extends Attributes, T, P> {
		
		/**
		 * To get access of internal collection of {@link ExtensionFormat}
		 * @param accessor
		 * @return
		 */
		ExtensionsStep<A, T, P> map(ExtensionFormatAccessor<A, T> accessor);
		
	}
	
	public static interface ExtensionsStep<A extends Attributes, T, P> {
		/**
		 * Marshals the collection of {@link ExtensionFormat} into a
		 * {@code Map<String, String>}
		 * @param marshaller
		 * @return
		 */
		HeaderMapStep<A, T, P> map(ExtensionMarshaller marshaller);
	}
	
	public static interface HeaderMapStep<A extends Attributes, T, P> {
		/**
		 * Marshals the map of attributes and extensions into a map of headers
		 * @param mapper
		 * @return
		 */
		DataMarshallerStep<A, T, P> map(FormatHeaderMapper mapper);
	}
	
	public static interface DataMarshallerStep<A extends Attributes, T, P> {
		/**
		 * Marshals the 'data' into payload
		 * @param marshaller
		 * @return
		 */
		BuilderStep<A, T, P> map(DataMarshaller<P, T> marshaller);
	}
	
	public static interface BuilderStep<A extends Attributes, T, P> {
		/**
		 * Builds the {@link Wire} to use for wire transfer
		 * @param builder
		 * @return
		 */
		EventStep<A, T, P> builder(WireBuilder<P, String, Object> builder);
	}
	
	private static final class Builder<A extends Attributes, T, P> implements 
		AttributeMarshalStep<A, T, P>,
		ExtensionsAccessorStep<A, T, P>,
		ExtensionsStep<A, T, P>,
		DataMarshallerStep<A, T, P>,
		HeaderMapStep<A, T, P>,
		BuilderStep<A, T, P>,
		EventStep<A, T, P>,
		MarshalStep<P> {
		
		private AttributeMarshaller<A> attributeMarshaller;
		private ExtensionFormatAccessor<A, T> extensionsAccessor;
		private ExtensionMarshaller extensionMarshaller;
		private FormatHeaderMapper headerMapper;
		private DataMarshaller<P, T> dataMarshaller;
		private WireBuilder<P, String, Object> wireBuilder;
		private Supplier<CloudEvent<A, T>> eventSupplier;

		@Override
		public ExtensionsAccessorStep<A, T, P> map(AttributeMarshaller<A> marshaller) {
			this.attributeMarshaller = marshaller;
			return this;
		}
		
		@Override
		public ExtensionsStep<A, T, P> map(ExtensionFormatAccessor<A, T> accessor) {
			this.extensionsAccessor = accessor;
			return this;
		}

		@Override
		public HeaderMapStep<A, T, P> map(ExtensionMarshaller marshaller) {
			this.extensionMarshaller = marshaller;
			return this;
		}
		
		@Override
		public DataMarshallerStep<A, T, P> map(FormatHeaderMapper mapper) {
			this.headerMapper = mapper;
			return this;
		}	
		
		@Override
		public BuilderStep<A, T, P> map(DataMarshaller<P, T> marshaller) {
			this.dataMarshaller = marshaller;
			return this;
		}
		
		@Override
		public EventStep<A, T, P> builder(WireBuilder<P, String, Object> builder) {
			this.wireBuilder = builder;
			return this;
		}

		@Override
		public MarshalStep<P> withEvent(Supplier<CloudEvent<A, T>> event) {
			this.eventSupplier = event;
			return this;
		}

		@Override
		public Wire<P, String, Object> marshal() {
			CloudEvent<A, T> event = eventSupplier.get();
			
			Map<String, String> attributesMap = 
					attributeMarshaller.marshal(event.getAttributes());
			
			Collection<ExtensionFormat> extensionsFormat = 
					extensionsAccessor.extensionsOf(event);
			
			Map<String, String> extensionsMap = 
					extensionMarshaller.marshal(extensionsFormat);
			
			Map<String, Object> headers = 
					headerMapper.map(attributesMap, extensionsMap);
			
			P payload = null;
			if(event.getData().isPresent()) {
				payload = dataMarshaller.marshal(event.getData().get(),
						headers);
			}
			
			return wireBuilder.build(payload, headers);
		}
	}
}
