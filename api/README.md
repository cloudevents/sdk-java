# CloudEvents API

The base classes, interfaces and low-level APIs to use CloudEvents.

## How to Use

### Binary Marshaller

The low-level API to marshal CloudEvents as binary content mode.

```java
import java.net.URI;
import java.time.ZonedDateTime;

import io.cloudevents.extensions.DistributedTracingExtension;
import io.cloudevents.extensions.ExtensionFormat;
import io.cloudevents.json.Json;
import io.cloudevents.v02.Accessor;
import io.cloudevents.v02.AttributesImpl;
import io.cloudevents.v02.CloudEventBuilder;
import io.cloudevents.v02.CloudEventImpl;
import io.cloudevents.v02.http.HeaderMapper;

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

/*Marshal the event as a Wire instance */
final Wire<String, String, Object> wire =
	BinaryMarshaller.<AttributesImpl, String, String>
      builder()
		.map(AttributesImpl::marshal)
		.map(Accessor::extensionsOf)
		.map(ExtensionFormat::marshal)
		.map(HeaderMapper::map)
		.map(Json.marshaller()::marshal)
		.builder(Wire<String, String, Object>::new)
		.withEvent(() -> ce)
		.marshal();

/*
 * Use the wire result, getting the headers map
 * and the actual payload
 */
wire.getHeaders(); //Map<String, Object>
wire.getPayload(); //Optional<String> which has the JSON

// Use in the transport binding: http, kafka, etc ...
```

### Binary Umarshaller

The low-level API to unmarshal CloudEvents from binary content mode.

```java
import java.util.HashMap;
import java.util.Map;

import io.cloudevents.CloudEvent;
import io.cloudevents.extensions.DistributedTracingExtension;
import io.cloudevents.json.Json;
import io.cloudevents.v02.AttributesImpl;
import io.cloudevents.v02.CloudEventBuilder;
import io.cloudevents.v02.http.AttributeMapper;
import io.cloudevents.v02.http.ExtensionMapper;

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
	BinaryUnmarshaller.<AttributesImpl, Map, String>
	  builder()
		.map(AttributeMapper::map)
		.map(AttributesImpl::unmarshal)
		.map("application/json", Json.umarshaller(Map.class)::unmarshal)
		.map("text/plain", (payload, attributes) -> new HashMap<>())
		.next()
		.map(ExtensionMapper::map)
		.map(DistributedTracingExtension::unmarshall)
		.next()
		.builder(CloudEventBuilder.<Map>builder()::build)
		.withHeaders(() -> httpHeaders)
		.withPayload(() -> myPayload)
		.unmarshal();

/* Use the CloudEvent instance attributes, data and extensions */
event.getAttributes();
event.getData();
event.getExtensions();
```

### Structured Marshaller

The low-level API to marshal CloudEvents as structured content mode.

```java
import java.net.URI;
import java.time.ZonedDateTime;
import java.util.HashMap;

import io.cloudevents.extensions.DistributedTracingExtension;
import io.cloudevents.extensions.ExtensionFormat;
import io.cloudevents.json.Json;
import io.cloudevents.v02.Accessor;
import io.cloudevents.v02.AttributesImpl;
import io.cloudevents.v02.CloudEventBuilder;
import io.cloudevents.v02.CloudEventImpl;
import io.cloudevents.v02.http.HeaderMapper;

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

final Wire<String, String, Object> wire =
  StructuredMarshaller.<AttributesImpl, String, String>
    builder()
      .mime("Content-Type", "application/cloudevents+json")
      .map(event -> {
        return Json.marshaller().marshal(event, new HashMap<>());
      })
      .map(Accessor::extensionsOf)
      .map(ExtensionFormat::marshal)
      .map(HeaderMapper::map)
      .withEvent(() -> ce)
      .marshal();

/*
 * Use the wire result, getting the headers map
 * and the actual payload
 */
wire.getHeaders(); //Map<String, Object>
wire.getPayload(); //Optional<String> which has the JSON

// Use in the transport binding: http, kafka, etc ...
```

### Structured Unmarshaller

TODO
