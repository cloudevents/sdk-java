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
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import io.cloudevents.Attributes;
import io.cloudevents.CloudEvent;
import io.cloudevents.format.builder.HeadersStep;
import io.cloudevents.format.builder.PayloadStep;
import io.cloudevents.format.builder.UnmarshalStep;
import io.cloudevents.fun.DataUnmarshaller;
import io.cloudevents.fun.EnvelopeUnmarshaller;
import io.cloudevents.fun.ExtensionUmarshaller;
import io.cloudevents.fun.FormatExtensionMapper;

/**
 * 
 * @author fabiojose
 *
 */
public class StructuredUnmarshaller {
	StructuredUnmarshaller() {}
	
	public static <A extends Attributes, T, P> DataUnmarshallerStep<A, T, P>
			builder() {
		return new Builder<>();
	}
	
	public static interface DataUnmarshallerStep<A extends Attributes, T, P> {
		DataUnmarshallerStep<A, T, P> map(String mime, DataUnmarshaller<P, T, A> unmarshaller);
		ExtensionMapperStep<A, T, P> next();
	}
	
	public static interface ExtensionMapperStep<A extends Attributes, T, P> {
		EnvelopeUnmarshallerStep<A, T, P> skip();
		ExtensionUnmarshallerStep<A, T, P> map(FormatExtensionMapper mapper);
	}
	
	public static interface ExtensionUnmarshallerStep<A extends Attributes, T, P> {
		ExtensionUnmarshallerStep<A, T, P> map(ExtensionUmarshaller unmarshaller);
		EnvelopeUnmarshallerStep<A, T, P> next();
	}
	
	public static interface EnvelopeUnmarshallerStep<A extends Attributes, T, P> {
		HeadersStep<A, T, P> map(EnvelopeUnmarshaller<A, T, P> unmarshaller);
	}
	
	private static final class Builder<A extends Attributes, T, P> implements
		DataUnmarshallerStep<A, T, P>,
		ExtensionMapperStep<A, T, P>,
		ExtensionUnmarshallerStep<A, T, P>,
		EnvelopeUnmarshallerStep<A, T, P>,
		HeadersStep<A, T, P>, 
		PayloadStep<A, T, P>,
		UnmarshalStep<A, T>{
		
		private final Map<String, DataUnmarshaller<P, T, A>> dataUnmarshallers = 
				new HashMap<>();
		
		private FormatExtensionMapper extensionMapper;
		
		private Set<ExtensionUmarshaller> extensionUnmarshallers = 
				new HashSet<>();
		
		private EnvelopeUnmarshaller<A, T, P> unmarshaller;
		
		private Supplier<Map<String, Object>> headersSupplier;
		
		private Supplier<P> payloadSupplier;

		@Override
		public DataUnmarshallerStep<A, T, P> map(String mime, 
				DataUnmarshaller<P, T, A> unmarshaller) {
			Objects.requireNonNull(mime);
			Objects.requireNonNull(unmarshaller);
			
			dataUnmarshallers.put(mime, unmarshaller);
			
			return this;
		}

		@Override
		public Builder<A, T, P> next() {
			return this;
		}

		@Override
		public EnvelopeUnmarshallerStep<A, T, P> skip() {
			return this;
		}

		@Override
		public ExtensionUnmarshallerStep<A, T, P> map(FormatExtensionMapper mapper) {
			Objects.requireNonNull(mapper);
			
			this.extensionMapper = mapper;
			
			return this;
		}

		@Override
		public ExtensionUnmarshallerStep<A, T, P> map(ExtensionUmarshaller unmarshaller) {
			Objects.requireNonNull(unmarshaller);
			
			this.extensionUnmarshallers.add(unmarshaller);
			
			return this;
		}

		@Override
		public HeadersStep<A, T, P> map(EnvelopeUnmarshaller<A, T, P> unmarshaller) {
			Objects.requireNonNull(unmarshaller);
			
			this.unmarshaller = unmarshaller;
			
			return this;
		}

		@Override
		public PayloadStep<A, T, P> withHeaders(Supplier<Map<String, Object>> headers) {
			Objects.requireNonNull(headers);
			
			this.headersSupplier = headers;
			
			return this;
		}

		@Override
		public UnmarshalStep<A, T> withPayload(Supplier<P> payload) {
			Objects.requireNonNull(payload);
			
			this.payloadSupplier = payload;
			
			return this;
		}

		@Override
		public CloudEvent<A, T> unmarshal() {
			
			Map<String, Object> headers = headersSupplier.get();
			P payload = payloadSupplier.get();
			
			final Map<String, String> extensionsMap = 
				Optional.ofNullable(extensionMapper)
					.map(mapper -> mapper.map(headers))
					.orElse(new HashMap<>());
			
			//TODO How to inject this list into result
			
			CloudEvent<A, T> result = 
				unmarshaller.unmarshal(payload, 
				  () -> 
					extensionUnmarshallers.stream()
					.map(unmarshaller ->
						unmarshaller.unmarshal(extensionsMap))
					.filter(Optional::isPresent)
					.map(Optional::get)
					.collect(Collectors.toList()));
			
			return result;
		}
		
	}
}
