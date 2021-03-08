# Spring Reactive + CloudEvents sample

## Build

```shell
mvn package
```

## Start HTTP Server

```shell
mvn spring-boot:run
```

You can try sending a request using curl, and it echos back a cloud event the same body and with new `ce-*` headers:

```shell
curl -v -d '{"value": "Foo"}' \
    -H'Content-type: application/json' \
    -H'ce-id: 1' \
    -H'ce-source: cloud-event-example' \
    -H'ce-type: my.application.Foo' \
    -H'ce-specversion: 1.0' \
    http://localhost:8080/event
```

It also accepts data in "structured" format:

```shell
curl -v -H'Content-type: application/cloudevents+json' \
    -d '{"data": {"value": "Foo"},
         "id: 1,
         "source": "cloud-event-example"
         "type": "my.application.Foo"
         "specversion": "1.0"}' \
    http://localhost:8080/event
```

The `/event endpoint is implemented like this (the request and response are modelled directly as a `CloudEvent`):

```java
@PostMapping("/event")
public Mono<CloudEvent> event(@RequestBody Mono<CloudEvent> body) {
	return ...;
}
```

and to make that work we need to install the codecs:

```java
@Configuration
public static class CloudEventHandlerConfiguration implements CodecCustomizer {

	@Override
	public void customize(CodecConfigurer configurer) {
		configurer.customCodecs().register(new CloudEventHttpMessageReader());
		configurer.customCodecs().register(new CloudEventHttpMessageWriter());
	}

}
```

The same feature in Spring MVC is provided by the `CloudEventHttpMessageConverter`.


The `/foos` endpoint does the same thing. It doesn't use the `CloudEvent` data type directly, but instead models the request and response body as a `Foo` (POJO type):

```java
@PostMapping("/foos")
public ResponseEntity<Foo> echo(@RequestBody Foo foo, @RequestHeader HttpHeaders headers) {
	...
}
```

Note that this endpoint only accepts "binary" format cloud events (context in HTTP headers like in the first example above). It translates the `HttpHeaders` to `CloudEventContext` using a utility class provided by `cloudevents-spring`.
