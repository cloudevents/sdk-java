# CloudEvents API

The base classes, interfaces and low-level APIs to use CloudEvents.

## How to Use

Here we will see how to use the pre-configure marshallers and unmarshallers.

### Binary Marshaller

The high-level API to marshal CloudEvents as binary content mode.

```java
import java.net.URI;
import java.time.ZonedDateTime;

import io.cloudevents.extensions.DistributedTracingExtension;
import io.cloudevents.extensions.ExtensionFormat;
import io.cloudevents.format.Wire;
import io.cloudevents.v02.CloudEventBuilder;
import io.cloudevents.v02.CloudEventImpl;

//...

/*Create a tracing extension*/
final DistributedTracingExtension dt =
    new DistributedTracingExtension();

dt.setTraceparent("0");
dt.setTracestate("congo=4");

/*Format it as extension format*/
final ExtensionFormat tracing =
    new DistributedTracingExtension.Format(dt);

/* Build a CloudEvent instance */
CloudEventImpl<String> ce =
	CloudEventBuilder.<String>builder()
		.withType("com.github.pull.create")
		.withSource(URI.create("https://github.com/cloudevents/spec/pull"))
		.withId("A234-1234-1234")					
		.withSchemaurl(URI.create("http://my.br"))
		.withTime(ZonedDateTime.now())
		.withContenttype("text/plain")
		.withData("my-data")
		.withExtension(tracing)
		.build();

/* Marshal the event as a Wire instance */
Wire<String, String, String> wire =
	Marshallers.<String>
	  binary()
		.withEvent(() -> ce)
		.marshal();

/*
 * Use the wire result, getting the headers map
 * and the actual payload
 */
wire.getHeaders(); //Map<String, String>
wire.getPayload(); //Optional<String> which has the JSON

// Use in the transport binding: http, kafka, etc ...
```

### Binary Umarshaller

The high-level API to unmarshal CloudEvents from binary content mode.

```java
import java.util.HashMap;
import java.util.Map;

import io.cloudevents.CloudEvent;
import io.cloudevents.extensions.DistributedTracingExtension;
import io.cloudevents.v02.AttributesImpl;
import io.cloudevents.v02.CloudEventBuilder;
import io.cloudevents.v02.http.Unmarshallers;

// . . .

/* The HTTP headers example */
Map<String, Object> httpHeaders = new HashMap<>();
httpHeaders.put("ce-specversion", "0.2");
httpHeaders.put("ce-type", "com.github.pull.create");
httpHeaders.put("ce-source", "https://github.com/cloudevents/spec/pull");
httpHeaders.put("ce-id", "A234-1234-1234");
httpHeaders.put("ce-time", "2018-04-05T17:31:00Z");
httpHeaders.put("ce-schemaurl", "http://my.br");
httpHeaders.put("my-ext", "my-custom extension");
httpHeaders.put("traceparent", "0");
httpHeaders.put("tracestate", "congo=4");
httpHeaders.put("Content-Type", "application/json");

/* The payload */
String myPayload = "{\"foo\" : \"rocks\", \"name\" : \"jocker\"}";

/* Unmarshals as CloudEvent instance */
CloudEvent<AttributesImpl, Map> event =
  Unmarshallers.binary(Map.class)
    .withHeaders(() -> httpHeaders)
    .withPayload(() -> myPayload)
    .unmarshal();

/* Use the CloudEvent instance attributes, data and extensions */
event.getAttributes();
event.getData();
event.getExtensions();
```

### Structured Marshaller

The high-level API to marshal CloudEvents as structured content mode.

```java
import java.net.URI;
import java.time.ZonedDateTime;

import io.cloudevents.extensions.DistributedTracingExtension;
import io.cloudevents.extensions.ExtensionFormat;
import io.cloudevents.v02.http.Marshallers;

// . . .

final DistributedTracingExtension dt =
    new DistributedTracingExtension();
dt.setTraceparent("0");
dt.setTracestate("congo=4");

final ExtensionFormat tracing =
		new DistributedTracingExtension.Format(dt);

final CloudEventImpl<String> ce =
		CloudEventBuilder.<String>builder()
			.withType("com.github.pull.create")
			.withSource(URI.create("https://github.com/cloudevents/spec/pull"))
			.withId("A234-1234-1234")
			.withSchemaurl(URI.create("http://my.br"))
			.withTime(ZonedDateTime.now())
			.withContenttype("text/plain")
			.withData("my-data")
			.withExtension(tracing)
			.build();

final Wire<String, String, String> wire =
  Marshallers.<String>structured()
      .withEvent(() -> ce)
      .marshal();

/*
 * Use the wire result, getting the headers map
 * and the actual payload
 */
wire.getHeaders(); //Map<String, String>
wire.getPayload(); //Optional<String> which has the JSON

// Use in the transport binding: http, kafka, etc ...
```

### Structured Unmarshaller

The high-level API to unmarshal CloudEvents as structured content mode.

```java
import java.util.HashMap;
import java.util.Map;

import io.cloudevents.CloudEvent;
import io.cloudevents.v02.AttributesImpl;

// . . .

/* The HTTP Headers */
Map<String, Object> httpHeaders = new HashMap<>();
httpHeaders.put("Content-Type", "application/cloudevents+json");

/* Distributed Tracing */
httpHeaders.put("traceparent", "0x200");
httpHeaders.put("tracestate", "congo=9");


/* JSON Payload */
String payload = "{\"data\":{\"wow\":\"yes!\"},\"id\":\"x10\",\"source\":\"/source\",\"specversion\":\"0.2\",\"type\":\"event-type\",\"contenttype\":\"application/json\"}";

/* Unmarshalling . . . */
CloudEvent<AttributesImpl, Map> event =
		Unmarshallers.structured(Map.class)
			.withHeaders(() -> httpHeaders)
			.withPayload(() -> payload)
			.unmarshal();

/* Use the event instance */
event.getAttributes();
event.getExtensions();
event.getData();
```

## Low-level (Un)Marshalling

When you need to process different formats, instead of JSON, or if you have a custom extension implementation. You must develop your own (un)marshallers.

We provide a way to make it easy, well, or with less pain at all.

Let us introduce the step builders:

- [BinaryMarshaller](./src/main/java/io/cloudevents/format/BinaryMarshaller.java)
- [StructuredMarshaller](./src/main/java/io/cloudevents/format/StructuredMarshaller.java)
- [BinaryUnmarshaller](./src/main/java/io/cloudevents/format/BinaryUnmarshaller.java)
- [StructuredUnmarshaller](./src/main/java/io/cloudevents/format/StructuredUnmarshaller.java)

All of them follow the [step builder pattern](https://java-design-patterns.com/patterns/step-builder/), that helps a lot when you are new in the data (un)marshalling. To support them we have a [bunch of functional interfaces](./src/main/java/io/cloudevents/fun), used by each step.

### Marshaller

Well, this is how build marshaller using the low-level API.

**Binary Marshaller**

```java
/*
 * The imports used by the example bellow
 */
import io.cloudevents.extensions.ExtensionFormat;
import io.cloudevents.format.BinaryMarshaller;
import io.cloudevents.format.Wire;
import io.cloudevents.format.builder.EventStep;
import io.cloudevents.json.Json;
import io.cloudevents.json.types.Much;
import io.cloudevents.v02.Accessor;
import io.cloudevents.v02.AttributesImpl;

// . . .

/*
 * Step 0. Define the types - there are four
 *   - Type 1 -> AttributesImpl: the implementation of attributes
 *   - Type 2 -> Much..........: the type CloudEvents' 'data'
 *   - Type 3 -> String........: the type of payload that will result of marshalling
 *   - Type 4 -> String........: the type of headers values. String for HTTP, byte[] for Kafka . . .
 */
EventStep<AttributesImpl, Much, String, String> builder =
	BinaryMarshaller.<AttributesImpl, Much, String, String>
	  builder()
	  	/*
	  	 * Step 1. The attributes marshalling
	  	 *   - in this step we must provide an impl able to marshal AttributesImpl into a
	  	 *   Map<String, String>
	  	 */
		.map(AttributesImpl::marshal)

		/*
		 * Step 2. Access the internal list of extensions
		 *   - here we must provide an accessor for the internal list of extensions
		 */
		.map(Accessor::extensionsOf)

		/*
		 * Step 3. The extensions marshalling
		 *   - we must provide an impl able to marshal a Collection<ExtensionFormat> into a
		 *   Map<String, String>
		 */
		.map(ExtensionFormat::marshal)

		/*
		 * Step 4. Mapping to headers
		 *  - provide an impl able to map from attributes and extensions into a
		 *  Map<String, String>, the headers of transport binding.
		 */
		.map(HeaderMapper::map)

		/*
		 * Step 5. The data marshaller
		 *   - provider an impl able to marshal the CloudEvents' data into payload
		 *   for transport
		 */
		.map(Json.<Much, String>marshaller()::marshal)

		/*
		 * Step 6. The wire builder
		 *   - to make easy to get the marshalled paylaod and the headers we
		 *   have the Wire
		 *   - here must to provide a way to create new instance of Wire
		 *   - now we get the EventStep<AttributesImpl, Much, String, String>, a common step
		 *   that every marshaller returns
		 *   - from here we just call withEvent() and marshal() methods
		 */
		.builder(Wire<String, String, String>::new);

/*
 * Using the marshaller
 */
Wire<String, String, String> wire =
	builder
		.withEvent(() -> myEvent)
		.marshal();
}
```

**Structured Marshaller**

```java
/*
 * The imports used by the example bellow
 */
import java.util.HashMap;

import io.cloudevents.CloudEvent;
import io.cloudevents.extensions.ExtensionFormat;
import io.cloudevents.format.StructuredMarshaller;
import io.cloudevents.format.builder.EventStep;
import io.cloudevents.json.Json;
import io.cloudevents.json.types.Much;
import io.cloudevents.v02.Accessor;
import io.cloudevents.v02.AttributesImpl;

// . . .

/*
 * Step 0. Define the types - there are four
 *   - Type 1 -> AttributesImpl: the implementation of attributes
 *   - Type 2 -> Much..........: the type CloudEvents' 'data'
 *   - Type 3 -> String........: the type of payload that will result of marshalling
 *   - Type 4 -> String........: the type of headers values. String for HTTP, byte[] for Kafka . . .
 */
EventStep<AttributesImpl, Much, String, String> builder =
  StructuredMarshaller.<AttributesImpl, Much, String, String>
    builder()
	/*
	 * Step 1. Setting the media type for the envelope
	 *   - here we must to say the name of media type header and it's value
	 */
	.mime("Content-Type", "application/cloudevents+json")

	/*
	 * Step 2. The marshaller for envelope
	 *   - we must provide an impl able to marshal the cloudevents envelope
	 */
	.map((event) -> {
		return Json.<CloudEvent<AttributesImpl, Much>, String>
					marshaller().marshal(event, new HashMap<>());
	})

    /*
     * Step 3. Access the internal list of extensions
     *   - here we must provide an accessor for the internal list of extensions
     */
	.map(Accessor::extensionsOf)

    /*
     * Step 4. The extensions marshalling
     *   - we must provide an impl able to marshal a Collection<ExtensionFormat> into a Map<String, String>
     */
	.map(ExtensionFormat::marshal)

    /*
     * Step 5. Mapping to headers
     *  - provide an impl able to map from attributes and extensions into a Map<String, String>, the headers of transport binding.
     *  - now we get the EventStep<AttributesImpl, Much, String, String>, a common step that every marshaller returns
     *   - from here we just call withEvent() and marshal() methods
     */
	.map(HeaderMapper::map);

/*
 * Using the marshaller
 */
Wire<String, String, String> wire =
    builder
        .withEvent(() -> myEvent)
        .marshal();

```

### Unmarshaller

**Binary Unmarshaller**

```java
/*
* The imports used by the example bellow
*/
import io.cloudevents.CloudEvent;
import io.cloudevents.extensions.DistributedTracingExtension;
import io.cloudevents.format.BinaryUnmarshaller;
import io.cloudevents.format.builder.HeadersStep;
import io.cloudevents.json.Json;
import io.cloudevents.json.types.Much;
import io.cloudevents.v02.AttributesImpl;
import io.cloudevents.v02.CloudEventBuilder;

// . . .

/*
 * Step 0. Define the types - there are three
 *   - Type 1 -> AttributesImpl: the implementation of attributes
 *   - Type 2 -> Much..........: the type CloudEvents' 'data'
 *   - Type 3 -> String........: the type of payload used in the unmarshalling
 */
HeadersStep<AttributesImpl, Much, String> builder =
	BinaryUnmarshaller.<AttributesImpl, Much, String>
	  builder()
	  	/*
	  	 * Step 1. Mapping from headers Map to attributes one
	  	 *   - we must provide a mapper able to map from transport headers to a map of attributes
	  	 *   - this like a translation, removing the prefixes or unknow names for example
	  	 */
		.map(AttributeMapper::map)

		/*
		 * Step 2. The attributes ummarshalling
		 *   - we must provide an impl able to unmarshal a Map of attributes into
		 *   an instance of Attributes
		 */
		.map(AttributesImpl::unmarshal)

		/*
		 * Step 3. The data umarshaller
		 *   - we may provive more than one unmarshaller per media type
		 *   - we must provide an impl able to unmashal the payload into
		 *   the actual 'data' type instance
		 */
		.map("application/json", Json.umarshaller(Much.class)::unmarshal)

		/*
		 * Step 3'. Another data unmarshaller
		 */
		.map("media type", (payload, headers) -> {

			return null;
		})

		/*
		 * When we are ok with data unmarshallers we call next()
		 */
		.next()

		/*
		 * Step 4. The extension mapping
		 *   - we must provider an impl able to map from transport headers to map of extensions
		 *   - we may use this for extensions that lives in the transport headers
		 */
		.map(ExtensionMapper::map)

		/*
		 * Step 5. The extension unmarshaller
		 *   - we must provide an impl able to unmarshal from map of extenstions into
		 *   actual ones
		 */
		.map(DistributedTracingExtension::unmarshall)

		/*
		 * Step 5'. Another extension unmarshaller
		 */
		.map((extensionsMap) -> {

			return null;
		})

		/*
		 * When we are ok with extensions unmarshallers we call next()
		 */
		.next()

		/*
		 * Step 6. The CloudEvent builder
		 *   - we must provide an impl able to take the extensions, data and attributes
		 *   and build CloudEvent instances
		 *   - now we get the HeadersStep<AttributesImpl, Much, String>, a common step
		 *   that event unmarshaller must returns
		 *   - from here we just call withHeaders(), withPayload() and unmarshal()
		 */
		.builder(CloudEventBuilder.<Much>builder()::build);

/*
 * Using the unmarshaller
 */
CloudEvent<AttributesImpl, Much> myEvent =
	builder
		.withHeaders(() -> transportHeaders)
		.withPayload(() -> payload)
		.unmarshal();
```

**Structured Unmarshaller**

```java
/*
 * Step 0. Define the types - there are three
 *   - Type 1 -> AttributesImpl: the implementation of attributes
 *   - Type 2 -> Much..........: the type CloudEvents' 'data'
 *   - Type 3 -> String........: the type of payload used in the unmarshalling
 */
HeadersStep<AttributesImpl, Much, String> step =
	StructuredUnmarshaller.<AttributesImpl, Much, String>
	  builder()
	  /*
		 * Step 1. The extension mapping
		 *   - we must provider an impl able to map from transport headers to map of extensions
		 *   - we may use this for extensions that lives in the transport headers
		 */
		.map(ExtensionMapper::map)

		/*
		 * Step 2. The extension unmarshaller
		 *   - we must provide an impl able to unmarshal from map of extenstions into actual ones
		 */
		.map(DistributedTracingExtension::unmarshall)

		/*
		 * Step 2'. When we are ok with extension unmarshallers, call next()
		 */
		.next()

		/*
		 * Step 3. Envelope unmarshaller
		 *   - we must provide an impl able to unmarshal the envelope into cloudevents
		 *   - now we get the HeadersStep<AttributesImpl, Much, String>, a common step that event unmarshaller must returns
		 *   - from here we just call withHeaders(), withPayload() and unmarshal()
		 */
		.map((payload, extensions) -> {			
			CloudEventImpl<Much> event =
				Json.<CloudEventImpl<Much>>
					decodeValue(payload, CloudEventImpl.class, Much.class);

			CloudEventBuilder<Much> builder =
				CloudEventBuilder.<Much>builder(event);

			extensions.get().forEach(extension -> {
				builder.withExtension(extension);
			});

			return builder.build();
		});

/*
 * Using the unmarshaller
 */
CloudEvent<AttributesImpl, Much> myEvent =
	step
		.withHeaders(() -> transportHeaders)
		.withPayload(() -> payload)
		.unmarshal();
```
