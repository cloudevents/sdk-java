# CloudEvents API

[![Javadocs](http://www.javadoc.io/badge/io.cloudevents/cloudevents-api.svg?color=green)](http://www.javadoc.io/doc/io.cloudevents/cloudevents-api)

For Maven based projects, use the following dependency:

```xml
<dependency>
    <groupId>io.cloudevents</groupId>
    <artifactId>cloudevents-api</artifactId>
    <version>2.0.0.RC1</version>
</dependency>
```

## Features

This package provides the base interfaces used by the SDK. In particular:

- `CloudEvent` is the main interface representing a read only CloudEvent in-memory representation
- `Extension` represents a _materialized_ in-memory representation of a CloudEvent extension
- `SpecVersion` is an enum of CloudEvents' specification versions supported by this SDK version.
- `CloudEventVisitor`/`CloudEventVisitable` are the interfaces used by the SDK to implement protocol bindings/event formats.
   A 3rd party implementer can implement these interfaces directly in its `CloudEvent` in order
   to customize/implement efficiently the marshalling/unmarshalling process.
   These interfaces are optional and, if your `CloudEvent` doesn't implement it, a default implementation is provided by the SDK.

`cloudevents-core` provides the implementation of these interfaces, and a 3rd party implementer can grab this package
to implement specialized CloudEvent in-memory representations.
