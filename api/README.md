# CloudEvents API

For Maven based projects, use the following to configure `cloudevents-api`:

```xml
<dependency>
    <groupId>io.cloudevents</groupId>
    <artifactId>cloudevents-api</artifactId>
    <version>2.0.0-SNAPSHOT</version>
</dependency>
```

## Features

This package provides the base interfaces used by the SDK. In particular:

- `CloudEvent` is the main interface representing a read only CloudEvent in memory representation
- `Extension` represents a _materialized_ in memory representation of a CloudEvent extension
- `SpecVersion` represents the different CloudEvent specification versions supported by this SDK version.
- `CloudEventVisitor`/`CloudEventVisitable` are the main interfaces used by the SDK to implement protocol bindings/event formats.
   If your `CloudEvent` doesn't implement it, a default implementation is provided by the SDK.

The implementation of these interfaces are provided by `cloudevents-core` and a 3rd party implementer can grab this package
to implement specialized CloudEvent in memory representations.
