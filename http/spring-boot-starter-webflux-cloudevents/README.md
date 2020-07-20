# HTTP Protocol Binding for Spring Boot Webflux (Reactive Stack)

For Maven based projects, use the following to configure the CloudEvents Spring Boot Binding:

```xml
<dependency>
    <groupId>io.cloudevents</groupId>
    <artifactId>spring-boot-starter-web-cloudevents</artifactId>
    <version>2.0.0-milestone1</version>
</dependency>
```

## Receiving CloudEvents
Below is a sample on how to read and write CloudEvents from a Post Request Headers and Body:

```java
    @PostMapping
    public String recieveCloudEvent(@RequestHeader Map<String, String> headers, @RequestBody Object body) {
      // Create a CloudEvent from Header and Body coming in the request
      CloudEvent cloudEvent = CloudEventsHelper.parseFromRequest(headers, body);


    }
```


## Sending CloudEvents


Below is a sample on how to use the client to send a CloudEvent:

```java
  // Create the CloudEvent with the builder
  final CloudEvent myCloudEvent = CloudEventBuilder.v03()
                .withId("ABC-123")
                .withType("my-first-cloud-event")
                .withSource(URI.create("knative-event-producer.default.svc.cluster.local"))
                .withData(SerializationUtils.serialize("{\"name\" : \"" + name + "-" + UUID.randomUUID().toString() + "\" }"))
                .withDataContentType("application/json")
                .build();

  // Use Reactive WebClient
  WebClient webClient = WebClient.builder().baseUrl(HOST).filter(logRequest()).build();
  // Use the Helper to Create the Post Request with your CloudEvent in it
  WebClient.ResponseSpec postCloudEvent = CloudEventsHelper.createPostCloudEvent(webClient, myCloudEvent);

  postCloudEvent.bodyToMono(String.class).doOnError(t -> t.printStackTrace())
                .doOnSuccess(s -> System.out.println("Result -> " + s)).subscribe();
```
