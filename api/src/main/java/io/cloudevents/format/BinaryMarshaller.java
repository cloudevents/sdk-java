package io.cloudevents.format;

import java.net.URI;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import io.cloudevents.Attributes;
import io.cloudevents.CloudEvent;
import io.cloudevents.extensions.DistributedTracingExtension;
import io.cloudevents.extensions.ExtensionFormat;
import io.cloudevents.fun.AttributeMarshaller;
import io.cloudevents.fun.BinaryFormatHeaderMapper;
import io.cloudevents.fun.DataMarshaller;
import io.cloudevents.fun.ExtensionMarshaller;
import io.cloudevents.fun.FormatBuilder;
import io.cloudevents.json.Json;
import io.cloudevents.v02.Accessor;
import io.cloudevents.v02.AttributesImpl;
import io.cloudevents.v02.CloudEventBuilder;
import io.cloudevents.v02.CloudEventImpl;
import io.cloudevents.v02.http.BinaryFormatHeaderMapperImpl;

/**
 * 
 * @author fabiojose
 *
 */
public class BinaryMarshaller<P, T, A extends Attributes> {
	
	public static <P, T, A extends Attributes> EventStep<P, T, A> builder() {
		return new Builder<>();
	}

	public static interface EventStep<P, T, A extends Attributes> {
		AttributeMarshalStep<P, T, A> 
			withEvent(Supplier<CloudEvent<A, T>> event);
	}
	
	public static interface AttributeMarshalStep<P, T, A extends Attributes> {
		ExtensionsStep<P, T, A> attributes(AttributeMarshaller<A> marshaller);
	}
	
	public static interface ExtensionsStep<P, T, A extends Attributes> {
		HeaderMapStep<P, T, A> extensions(ExtensionMarshaller marshaller);
	}
	
	public static interface HeaderMapStep<P, T, A extends Attributes> {
		PayloadStep<P, T, A> headers(BinaryFormatHeaderMapper mapper);
	}
	
	public static interface PayloadStep<P, T, A extends Attributes> {
		Build<P, T, A> payload(DataMarshaller<P, T> marshaller);
	}
	
	public static interface Build<P, T, A extends Attributes> {
		Format<P> build(FormatBuilder<P> builder);
	}
	
	public static class Builder<P, T, A extends Attributes> implements 
		EventStep<P, T, A>, AttributeMarshalStep<P, T, A>,
		ExtensionsStep<P, T, A>, HeaderMapStep<P, T, A>,
		PayloadStep<P, T, A>, Build<P, T, A> {
		
		private CloudEvent<A, T> event;
		
		private Map<String, String> attributesMap;
		
		private Map<String, Object> headers;
		
		private P payload;
		
		private Map<String, String> extensionsMap;
		
		@Override
		public AttributeMarshalStep<P, T, A> 
				withEvent(Supplier<CloudEvent<A, T>> event) {
			this.event = event.get();
			return this;
		}
		
		@Override
		public ExtensionsStep<P, T, A> attributes(AttributeMarshaller<A> marshaller) {
			this.attributesMap = marshaller.marshal(event.getAttributes());
			return this;
		}
		
		@Override
		public HeaderMapStep<P, T, A> extensions(ExtensionMarshaller marshaller) {
			this.extensionsMap = marshaller.marshal(Accessor.extensionsOf(event));
			return this;
		}

		@Override
		public PayloadStep<P, T, A> headers(BinaryFormatHeaderMapper mapper) {
			this.headers = mapper.map(attributesMap, extensionsMap);
			return this;
		}

		@Override
		public Build<P, T, A> payload(DataMarshaller<P, T> marshaller) {
			event.getData().ifPresent((data) -> {
				try {
					payload = marshaller.marshal(data, headers);
				}catch(Exception e) {
					throw new RuntimeException(e.getMessage(), e);
				}
			});
			return this;
		}

		@Override
		public Format<P> build(FormatBuilder<P> builder) {
			return builder.build(payload, headers);
		}
	}
	
	public static void main(String[] args) {
		
		final DistributedTracingExtension dt = new DistributedTracingExtension();
		dt.setTraceparent("0");
		dt.setTracestate("congo=4");
		
		final ExtensionFormat tracing = new DistributedTracingExtension.Format(dt);

		final CloudEventImpl<String> ce = 
			CloudEventBuilder.<String>builder()
				.withId("x10")
				.withSource(URI.create("/source"))
				.withType("event-type")
				.withSchemaurl(URI.create("/schema"))
				.withContenttype("text/plain")
				.withExtension(tracing)
				.withData("my-data")
				.build();
		
		Format<String> format =	
		BinaryMarshaller.<String, String, AttributesImpl>builder()
			.withEvent(() -> ce)
			.attributes(AttributesImpl.marshaller()::marshal)
			.extensions((extensions) -> {
				return extensions.stream()
					.map(ExtensionFormat::transport)
					.flatMap(t -> t.entrySet().stream())
					.collect(Collectors.toMap(Entry::getKey, Entry::getValue));
			})
			.headers(BinaryFormatHeaderMapperImpl.mapper()::map)
			.payload((data, headers) -> {
				return Json.encode(data);
			})
			.build((payload, headers) -> {
				return new Format<>(payload, headers);
			});
		
		System.out.println(format.getPayload());
		System.out.print(format.getHeaders());
	}
}
