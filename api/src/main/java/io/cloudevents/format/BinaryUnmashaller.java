package io.cloudevents.format;

import static java.util.stream.Collectors.toMap;

import java.util.ArrayList;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.cloudevents.Attributes;
import io.cloudevents.CloudEvent;
import io.cloudevents.extensions.DistributedTracingExtension;
import io.cloudevents.extensions.ExtensionFormat;
import io.cloudevents.extensions.InMemoryFormat;
import io.cloudevents.fun.BinaryFormatAttributeMapper;
import io.cloudevents.fun.BinaryFormatExtensionMapper;
import io.cloudevents.fun.AttributeUnmarshaller;
import io.cloudevents.fun.DataUnmarshaller;
import io.cloudevents.fun.EventBuilder;
import io.cloudevents.fun.ExtensionUmarshaller;
import io.cloudevents.json.Json;
import io.cloudevents.v02.http.BinaryFormatAttributeMapperImpl;
import io.cloudevents.v02.http.BinaryFormatExtensionMapperImpl;
import io.cloudevents.v02.AttributesImpl;
import io.cloudevents.v02.CloudEventBuilder;

/**
 * 
 * 
 * @author fabiojose
 *
 * @param <E>
 * @param <R>
 */
public class BinaryUnmashaller<P, T, A extends Attributes> {
	
	private BinaryUnmashaller(){}
	
	/**
	 * Gets a new builder instance
	 * @param <P> The payload type, ex: String
	 * @param <T> The 'data' type
	 * @param <A> The 'attributes' type
	 * @return
	 */
	public static <P, T, A extends Attributes> HeadersStep<P, T, A> builder() {
		return new Builder<>();
	}
	
	public interface HeadersStep<P, T, A extends Attributes> {
		/**
		 * Pass a supplier with the headers of binary format
		 * @param headers
		 * @return
		 */
		PayloadStep<P, T, A> withHeaders(Supplier<Map<String, Object>> headers);
	}
	
	public interface PayloadStep<P, T, A extends Attributes> {
		/**
		 * Pass a supplier thats provides the payload
		 * @param payload
		 * @return
		 */
		AttributeMapStep<P, T, A> withPayload(Supplier<P> payload);
	}
	
	public interface AttributeMapStep<P, T, A extends Attributes> {
		/**
		 * Maps the map of headers into map of attributes
		 * @param un
		 * @return
		 */
		AttributeUmarshallStep<P, T, A> attributes(BinaryFormatAttributeMapper un);
	}
	
	public interface AttributeUmarshallStep<P, T, A extends Attributes> {
		/**
		 * Unmarshals the map of attributes into actual ones
		 * @param un
		 * @return
		 */
		DataUnmarshallStep<P, T, A> attributes(AttributeUnmarshaller<A> un);
	}
	
	public interface DataUnmarshallStep<P, T, A extends Attributes> {
		/**
		 * Unmarshals the payload into actual 'data' type
		 * @param un
		 * @return
		 */
		ExtensionsMapStep<P, T, A> data(DataUnmarshaller<P, T, A> un);
	}
	
	public interface ExtensionsMapStep<P, T, A extends Attributes> {
		/**
		 * Maps the headers map into map of extensions
		 * @param mapper
		 * @return
		 */
		ExtensionsStepBegin<P, T, A> extensions(BinaryFormatExtensionMapper mapper);
	}
	
	public interface ExtensionsStepBegin<P, T, A extends Attributes> {
		/**
		 * Starts the block of extensions unmarshalling configuration
		 * @return
		 */
		ExtensionsStep<P, T, A> begin();
	}
	
	public interface ExtensionsStep<P, T, A extends Attributes> {
		/**
		 * Unmarshals a extension, based on the map of extensions
		 * @param un
		 * @return
		 */
		ExtensionsStep<P, T, A> extension(ExtensionUmarshaller un);
		
		/**
		 * Ends the block of extensions umarshalling
		 * @return
		 */
		Build<T, A> end();
	}
	
	public interface Build<T, A extends Attributes> {
		/**
		 * Builds the {@link CloudEvent} instance
		 * @param builder
		 * @return
		 */
		CloudEvent<A, T> build(EventBuilder<T, A> builder);
	}
	
	public static class Wrapper {
		private String name;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}
	
	public static class Dummy {
		private String foo;
		private Wrapper wrap;
		
		public Dummy() {
			this.wrap = new Wrapper();
			this.wrap.setName("common.name");
			this.setFoo("bar");
		}

		public String getFoo() {
			return foo;
		}

		public void setFoo(String foo) {
			this.foo = foo;
		}

		@JsonUnwrapped
		public Wrapper getWrap() {
			return wrap;
		}

		public void setWrap(Wrapper wrap) {
			this.wrap = wrap;
		}
	}
	
	public static class Builder<P, T, A extends Attributes> 
		implements HeadersStep<P, T, A>, PayloadStep<P, T, A>,
			AttributeMapStep<P, T, A>, AttributeUmarshallStep<P, T, A>,
			DataUnmarshallStep<P, T, A>,
			ExtensionsMapStep<P, T, A>, ExtensionsStepBegin<P, T, A>,
			ExtensionsStep<P, T, A>,
			Build<T, A> {
		
		private Map<String, Object> headers;
		private Map<String, String> attributesMap;
		
		private P payload;
		private T data;
		
		private A attributes;
		
		private Map<String, String> extensionsMap;
		private List<ExtensionFormat> extensions = new ArrayList<>();

		@Override
		public PayloadStep<P, T, A> withHeaders(Supplier<Map<String, Object>> headers) {
			
			this.headers = headers.get()
				.entrySet()
				.stream()
				.map((entry) -> 
					new SimpleEntry<>(entry.getKey(), entry.getValue()))
				.collect(toMap(Map.Entry::getKey,
						Map.Entry::getValue));
			
			return this;
		}
		
		@Override
		public AttributeMapStep<P, T, A> withPayload(Supplier<P> payload) {
			this.payload = payload.get();
			return this;
		}
		
		@Override
		public AttributeUmarshallStep<P, T, A> attributes(BinaryFormatAttributeMapper un) {
			this.attributesMap = un.map(headers);
			return this;
		}
		
		@Override
		public DataUnmarshallStep<P, T, A> attributes(AttributeUnmarshaller<A> un) {
			this.attributes = un.unmarshal(attributesMap);
			return this;
		}
		
		@Override
		public ExtensionsMapStep<P, T, A> data(DataUnmarshaller<P, T, A> un) {
			try {
				this.data = un.unmarshal(this.payload, this.attributes);
				return this;
			}catch(Exception e) {
				throw new RuntimeException(e.getMessage(), e);
			}
		}
		
		@Override
		public ExtensionsStepBegin<P, T, A> extensions(BinaryFormatExtensionMapper mapper) {
			this.extensionsMap = mapper.map(headers);
			return this;
		}
		
		@Override
		public ExtensionsStep<P, T, A> begin() {
			return this;
		}
		
		@Override
		public ExtensionsStep<P, T, A> extension(ExtensionUmarshaller un) {
			Optional<ExtensionFormat> ef = un.unmarshal(extensionsMap);		
			ef.ifPresent((value) ->{
				this.extensions.add(value);
			});
			return this;
		}
		
		@Override
		public Build<T, A> end() {
			return this;
		}

		@Override
		public CloudEvent<A, T> build(EventBuilder<T, A> builder) {
			return builder.build(data, attributes, extensions);
		}
		
	}
	
	public static void main(String[] args) throws Exception {
		
		Map<String, Object> myHeaders = new HashMap<>();
		myHeaders.put("ce-id", "0x11");
		myHeaders.put("ce-source", "/source");
		myHeaders.put("ce-specversion", "0.3");
		myHeaders.put("ce-type", "br.my");
		myHeaders.put("ce-time", "2019-09-16T20:49:00Z");
		myHeaders.put("ce-schemaurl", "http://my.br");
		myHeaders.put("my-ext", "my-custom extension");
		myHeaders.put("traceparent", "0");
		myHeaders.put("tracestate", "congo=4");
		myHeaders.put("Content-Type", "application/json");
		
		String myPayload = "{\"foo\" : \"rocks\", \"name\" : \"jocker\"}";
		
		//String payload = "{\"id\":\"0x19\",\"type\":\"aws.s3.object.created\",\"time\":\"2018-04-26T14:48:09.769Z\",\"source\":\"/source\",\"contenttype\":\"application/json\",\"specversion\":\"0.2\"}";
		
		ObjectMapper mapper = new ObjectMapper();
		System.out.println(mapper.writeValueAsString(new Dummy()));
		
		CloudEvent<AttributesImpl, Dummy> event = BinaryUnmashaller.<String, Dummy, AttributesImpl> 
			builder()
				.withHeaders(() -> myHeaders)
				.withPayload(() -> myPayload)
				.attributes(new BinaryFormatAttributeMapperImpl()::map)
				.attributes(AttributesImpl.unmarshaller()::unmarshal)
				.data(Json.umarshaller(Dummy.class)::unmarshal)
				.extensions(new BinaryFormatExtensionMapperImpl()::map)
				.begin()
					.extension((exts) -> {
						String key = "my-ext";
						String value = exts.get(key);
						
						ExtensionFormat result = null;
						if(null!= value) {
							result = ExtensionFormat.of(InMemoryFormat
									.of(key, value, Object.class), key, value);
						}
						return Optional.ofNullable(result);
					})
					.extension((exts) -> {
						String traceparent = exts.get("traceparent");
						String tracestate  = exts.get("tracestate");
						
						if(null!= traceparent && null!= tracestate) {
							DistributedTracingExtension dte = new DistributedTracingExtension();
							dte.setTraceparent(traceparent);
							dte.setTracestate(tracestate);
							
							InMemoryFormat inMemory = 
								InMemoryFormat.of("distributedTracing", dte, Object.class);
							
							return Optional.of(
								ExtensionFormat.of(inMemory, 
									new SimpleEntry<>("traceparent", traceparent),
									new SimpleEntry<>("tracestate", tracestate))
							);
							
						}
						
						return Optional.empty();	
					})
				.end()
				.build(CloudEventBuilder.<Dummy>builder()::build);
		
		System.out.println(event.getAttributes());
		System.out.println(event.getData());
		System.out.println(event.getExtensions());
	}
}
