# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/)

## [Unreleased]

TODO

## [0.3.1]

### Fixed

- Vulnerable dependency `com.fasterxml.jackson.core:jackson-databind`

## [0.3.0]

### Added
- [Attributes](./api/src/main/java/io/cloudevents/Attributes.java) marker
interface for context attributes
- Support for [Spec v0.3](https://github.com/cloudevents/spec/tree/v0.3)
- [ExtensionFormat](./api/src/main/java/io/cloudevents/ExtensionFormat.java)
interface for extensions
- [HTTP Marshallers](./api/src/main/java/io/cloudevents/v02/http/Marshallers.java) with bare minium marshallers for HTTP Transport Binding
- [HTTP Unmarshallers](./api/src/main/java/io/cloudevents/v02/http/Unmarshallers.java) with bare minium unmarshallers for HTTP Transport Binding
- [Kafka Marshallers](./kafka/src/main/java/io/cloudevents/v02/kafka/Marshallers.java) with bare minimum marshallers for Kafka Transport Binding
- [CloudEventsKafkaProducer](./kafka/src/main/java//io/cloudevents/kafka/CloudEventsKafkaProducer.java) The CloudEvents producer that uses Kafka Clients API
- [Kafka Unmarshallers](./kafka/src/main/java/io/cloudevents/v02/kafka/Unmarshallers.java) with bare minium unmarshallers for Kafka Transport Binding
- [CloudEventsKafkaConsumer](./kafka/src/main/java//io/cloudevents/kafka/CloudEventsKafkaConsumer.java) The CloudEvents consumer that uses Kafka Clients API
- [BinaryMarshaller](./api/src/main/java/io/cloudevents/format/BinaryMarshaller.java) To help in the case of you need to create your own marshallers for binary content mode
- [BinaryUnmarshaller](./api/src/main/java/io/cloudevents/format/BinaryUnmarshaller.java) To help in the case of you need to create your own unmarshallers for binary content mode
- [StructuredMarshaller](./api/src/main/java/io/cloudevents/format/StructuredMarshaller.java) To help in the case of you need to create your own marshallers for structured content mode
- [StructuredUnmarshaller](./api/src/main/java/io/cloudevents/format/StructuredUnmarshaller.java) To help in the case of you need to create your own unmarshallers for structured content mode


### Changed
- CloudEvent interface signature, breaking the backward compatibility
- Class: `io.cloudevents.v02.CloudEvent` moved to `io.cloudevents.v02.CloudEventImpl`
- Class: `io.cloudevents.v02.CloudEventImpl` had changed its signature, breaking the
backward compatibility
- Method: `io.cloudevents.json.Json.fromInputStream` had moved to Generics signature
- Class: `io.cloudevents.http.V02HttpTransportMappers` moved to
`io.cloudevents.v02.http.BinaryAttributeMapperImpl` and had changed is signature breaking the backward compatibility

### Removed
- Support for Spec v0.1
- Class: `io.cloudevents.impl.DefaultCloudEventImpl`, in favor of a impl for each
version
- Class: `io.cloudevents.CloudEventBuilder`, in favor of a builder for each version
- Enum: `io.cloudevents.SpecVersion`, in favor of specialization of specs
- Method: `io.cloudevents.json.Json.decodeCloudEvent`
- Class: `io.cloudevents.http.V01HttpTransportMappers` due the unsupported v0.1
- interface: `io.cloudevents.http.HttpTransportAttributes`, in favor of the new
abstract envelope signature
- interface: `io.cloudevents.Extension` in favor of
`io.cloudevents.extensions.ExtensionFormat`

[Unreleased]: https://github.com/cloudevents/sdk-java/compare/v0.3.1...HEAD
[0.3.1]: https://github.com/cloudevents/sdk-java/compare/v0.3.0...v0.3.1
[0.3.0]: https://github.com/cloudevents/sdk-java/compare/v0.2.1...v0.3.0
