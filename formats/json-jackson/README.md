# Json EventFormat implementation with Jackson

Implementation of [`EventFormat`](../../api/src/main/java/io/cloudevents/format/EventFormat.java) using Jackson.

For Maven:

```xml
<dependency>
    <groupId>io.cloudevents</groupId>
    <artifactId>cloudevents-json-jackson</artifactId>
    <version>2.0.0-SNAPSHOT </version>
</dependency>
```

## Usage

You don't need to perform any operation to configure the module, more than adding the dependency to your project:

```java
CloudEvent event = CloudEvent.buildV1()
  .withId("hello")
  .withType("example.vertx")
  .withSource(URI.create("http://localhost"))
  .build();

byte[] serialized = EventFormatProvider
  .getInstance()
  .resolveFormat("application/json")
  .serialize(event);
```
