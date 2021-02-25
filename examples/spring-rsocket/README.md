# Spring RSockets + CloudEvents sample

## Build

```shell
mvn package
```

## Start Server

```shell
mvn spring-boot:run
```

You can try sending a request using [rsc](https://github.com/making/rsc), and it echos back a cloud event the same body and with new `ce-*` headers:

```shell
rsc --request --dataMimeType=application/cloudevents+json --route=event \
    --data='{"data": {"value": "Foo"},
           "id": "1", 
           "source": "cloud-event-example", 
           "type": "my.application.Foo", 
           "specversion": "1.0"}' \
    --debug tcp://localhost:7000
```

The `event` endpoint is implemented like this (the request and response are modelled directly as a `CloudEvent`):

```java
@MessageMapping("event")
public Mono<CloudEvent> event(@RequestBody Mono<CloudEvent> body) {
	return ...;
}
```

and to make that work we need to install the codecs:

```java
@Bean
@Order(-1)
public RSocketStrategiesCustomizer cloudEventsCustomizer() {
	return new RSocketStrategiesCustomizer() {
		@Override
		public void customize(Builder strategies) {
			strategies.encoder(new CloudEventEncoder());
			strategies.decoder(new CloudEventDecoder());
		}
	};

}
```
