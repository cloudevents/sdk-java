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

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

import io.cloudevents.Attributes;
import io.cloudevents.CloudEvent;
import io.cloudevents.extensions.DistributedTracingExtension;
import io.cloudevents.extensions.ExtensionFormat;
import io.cloudevents.fun.EnvelopeMarshaller;
import io.cloudevents.fun.ExtensionFormatAccessor;
import io.cloudevents.fun.ExtensionMarshaller;
import io.cloudevents.fun.FormatHeaderMapper;
import io.cloudevents.json.Json;
import io.cloudevents.v02.AttributesImpl;
import io.cloudevents.v02.CloudEventBuilder;
import io.cloudevents.v02.CloudEventImpl;

/**
 * 
 * @author fabiojose
 *
 */
public class StructuredMarshaller {
	StructuredMarshaller() {}

	/*
	 * map("application/json", Json::marshaller()) // Data marshaller NOPE
	 * map("application/avro", Avro::marshaller()) // Data marshaller
	 * next()
	 * mime("")
	 * map(Json::marshaller) // CloudEvent marshaller
	 * withEvent(() -> ce)
	 * marshal() // returns a Wire instance
	 */
	
	/**
	 * 
	 * @param <A> The attributes type
	 * @param <T> The CloudEvents 'data' type
	 * @param <P> The CloudEvents marshaled envelope type 
	 * @return
	 */
	public static <A extends Attributes, T, P> MediaTypeStep<A, T, P>
			builder() {
		return new Builder<>();
	}
		
	public static interface MediaTypeStep<A extends Attributes, T, P> {
		/**
		 * Sets the media type of CloudEvents envelope
		 * @param headerName Example {@code Content-Type} for HTTP
		 * @param mediaType Example: {@code application/cloudevents+json}
		 */
		EnvelopeMarshallerStep<A, T, P> mime(String headerName, String mediaType);
	}
	
	public static interface EnvelopeMarshallerStep<A extends Attributes, T, P> {
		/**
		 * Sets the marshaller for the CloudEvent
		 * @param marshaller
		 */
		ExtensionAccessorStep<A, T, P> map(EnvelopeMarshaller<A, T, P> marshaller);
	}
	
	public static interface ExtensionAccessorStep<A extends Attributes, T, P> {
		/**
		 * To skip the extension special handling
		 */
		EventStep<A, T, P> skip();
		ExtensionMarshallerStep<A, T, P> map(ExtensionFormatAccessor<A, T> accessor);
	}
	
	public static interface ExtensionMarshallerStep<A extends Attributes, T, P> {
		HeaderMapperStep<A, T, P> map(ExtensionMarshaller marshaller);
	}
	
	public static interface HeaderMapperStep<A extends Attributes, T, P> {
		EventStep<A, T, P> map(FormatHeaderMapper mapper);
	}
	
	public static interface EventStep<A extends Attributes, T, P> {
		/**
		 * Sets the event which will be marshaled
		 * @param event
		 * @return
		 */
		MarshalStep<P> withEvent(Supplier<CloudEvent<A, T>> event);
	}
	
	public static interface MarshalStep<P> {
		/**
		 * Marshals the event to use in the wire transfer
		 */
		Wire<P, String, Object> marshal();
	}

	private static final class Builder<A extends Attributes, T, P> implements
		MediaTypeStep<A, T, P>,
		EnvelopeMarshallerStep<A, T, P>,
		ExtensionAccessorStep<A, T, P>,
		ExtensionMarshallerStep<A, T, P>,
		HeaderMapperStep<A, T, P>,
		EventStep<A, T, P>,
		MarshalStep<P>{
		
		private static final Map<String, String> NO_ATTRS = 
				new HashMap<>();
		
		private String headerName;
		private String mediaType;
		
		private EnvelopeMarshaller<A, T, P> marshaller;
		
		private ExtensionFormatAccessor<A, T> extensionAccessor;
		
		private ExtensionMarshaller extensionMarshaller;
		
		private FormatHeaderMapper headerMapper;
		
		private Supplier<CloudEvent<A, T>> event;
		
		@Override
		public EnvelopeMarshallerStep<A, T, P> mime(String headerName, String mediaType) {
			Objects.requireNonNull(headerName);
			Objects.requireNonNull(mediaType);
			
			this.headerName = headerName;
			this.mediaType = mediaType;
			return this;
		}

		@Override
		public ExtensionAccessorStep<A, T, P> map(EnvelopeMarshaller<A, T, P> marshaller) {
			Objects.requireNonNull(marshaller);
			
			this.marshaller = marshaller;
			return this;
		}
		
		@Override
		public EventStep<A, T, P> skip() {
			return this;
		}

		@Override
		public ExtensionMarshallerStep<A, T, P> map(ExtensionFormatAccessor<A, T> accessor) {
			Objects.requireNonNull(accessor);
			
			this.extensionAccessor = accessor;
			return this;
		}
		
		@Override
		public HeaderMapperStep<A, T, P> map(ExtensionMarshaller marshaller) {
			Objects.requireNonNull(marshaller);
			
			this.extensionMarshaller = marshaller;
			return this;
		}
		
		@Override
		public EventStep<A, T, P> map(FormatHeaderMapper mapper) {
			Objects.requireNonNull(mapper);
			
			this.headerMapper = mapper;
			return this;
		}

		@Override
		public MarshalStep<P> withEvent(Supplier<CloudEvent<A, T>> event) {
			Objects.requireNonNull(event);
			
			this.event = event;
			return this;
		}

		@Override
		public Wire<P, String, Object> marshal() {
			CloudEvent<A, T> ce = event.get();
			
			P payload = marshaller.marshal(ce);
			
			Map<String, Object> headers =
			Optional.ofNullable(extensionAccessor)
				.map(accessor -> accessor.extensionsOf(ce))
				.map(extensions -> extensionMarshaller.marshal(extensions))
				.map(extensions -> headerMapper.map(NO_ATTRS, extensions))
				.orElse(new HashMap<>());
			
			headers.put(headerName, mediaType);
			
			return new Wire<>(payload, headers);
		}
	}
	
	public static class Wrapper {
		private String name;
		
		public Wrapper() {}
		public Wrapper(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}
	
	public static void main(String[] args) {
		final DistributedTracingExtension dt = 
				new DistributedTracingExtension();
		dt.setTraceparent("0");
		dt.setTracestate("congo=4");
		
		final ExtensionFormat tracing = 
				new DistributedTracingExtension.Format(dt);
		
		final CloudEventImpl<Wrapper> ce = 
				CloudEventBuilder.<Wrapper>builder()
					.withId("x10")
					.withSource(URI.create("/source"))
					.withType("event-type")
					.withSchemaurl(URI.create("/schema"))
					.withContenttype("text/plain")
					.withData(new Wrapper("Jack"))
					.withExtension(tracing)
					.build();
		
		Wire<String, String, Object> wire =
			StructuredMarshaller.<AttributesImpl, Wrapper, String>
			  builder()
				.mime("Content-Type", "application/cloudevents+json")
				.map(event -> {
					return Json.marshaller().marshal(event, null);
				})
				.skip()
				.withEvent(() -> ce)
				.marshal();
	}
}
