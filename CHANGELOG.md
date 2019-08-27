# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/)

## [Unreleased]

### Added
- [Attributes](./api/src/main/java/io/cloudevents/Attributes.java) marker
interface for context attributes
- Support for [Spec v0.3](https://github.com/cloudevents/spec/tree/v0.3)
- [ExtensionFormat](./api/src/main/java/io/cloudevents/ExtensionFormat.java)
interface for extensions

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
