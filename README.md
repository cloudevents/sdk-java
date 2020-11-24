# Java SDK for CloudEvents API

[![License](https://img.shields.io/:license-Apache2-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.cloudevents/cloudevents-parent/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.cloudevents/cloudevents-parent)
[![Javadocs](http://www.javadoc.io/badge/io.cloudevents/cloudevents-core.svg?color=green)](http://www.javadoc.io/doc/io.cloudevents/cloudevents-core)

A Java API for the
[CloudEvents specification](https://github.com/cloudevents/spec)

Look at https://cloudevents.github.io/sdk-java/ for more documentation.

## Status

This SDK is considered **work in progress**. The community is working hard to
bring you a new major version of the SDK with major enhancements both to APIs
and to implementation.

If you want to know more about v1 of this SDK, check out the
[v1 readme](https://github.com/cloudevents/sdk-java/tree/1.x)

Stay tuned!

Supported features of the specification:

|                                         | [v0.3](https://github.com/cloudevents/spec/tree/v0.3) | [v1.0](https://github.com/cloudevents/spec/tree/v1.0) |
| :-------------------------------------: | :---------------------------------------------------: | :---------------------------------------------------: |
|            CloudEvents Core             |                  :heavy_check_mark:                   |                  :heavy_check_mark:                   |
|          AMQP Protocol Binding          |                          :x:                          |                          :x:                          |
|            - [Proton](amqp)             |                  :heavy_check_mark:                   |                  :heavy_check_mark:                   |
|            AVRO Event Format            |                          :x:                          |                          :x:                          |
|          HTTP Protocol Binding          |                  :heavy_check_mark:                   |                  :heavy_check_mark:                   |
|         - [Vert.x](http/vertx)          |                  :heavy_check_mark:                   |                  :heavy_check_mark:                   |
| - [Jakarta Restful WS](http/restful-ws) |                  :heavy_check_mark:                   |                  :heavy_check_mark:                   |
|          - [Basic](http/basic)          |                  :heavy_check_mark:                   |                  :heavy_check_mark:                   |
|            JSON Event Format            |                  :heavy_check_mark:                   |                  :heavy_check_mark:                   |
|    - [Jackson](formats/json-jackson)    |                  :heavy_check_mark:                   |                  :heavy_check_mark:                   |
|     [Kafka Protocol Binding](kafka)     |                  :heavy_check_mark:                   |                  :heavy_check_mark:                   |
|          MQTT Protocol Binding          |                          :x:                          |                          :x:                          |
|          NATS Protocol Binding          |                          :x:                          |                          :x:                          |
|                Web hook                 |                          :x:                          |                          :x:                          |

## Motivation

The [CloudEvents specification](https://github.com/cloudevents/spec) is a
vendor-neutral specification for defining the format of event data that is being
exchanged between different cloud systems. The specification basically defines
an abstract envelope for any event data payload, without knowing specific
implementation details of the actual underlying event. The current version of
the spec is at `1.0` and it describes a simple event format, which was
demonstrated at [KubeCon 2018](https://youtu.be/TZPPjAv12KU) using different
_Serverless platforms_, such as
[Apache Openwhisk](https://github.com/apache/incubator-openwhisk).

## Documentation

Documentation is available at https://cloudevents.github.io/sdk-java/

Javadocs are available on [javadoc.io](https://www.javadoc.io):

-   [cloudevents-api](https://www.javadoc.io/doc/io.cloudevents/cloudevents-api)
-   [cloudevents-core](https://www.javadoc.io/doc/io.cloudevents/cloudevents-core)
-   [cloudevents-json-jackson](https://www.javadoc.io/doc/io.cloudevents/cloudevents-json-jackson)
-   [cloudevents-http-basic](https://www.javadoc.io/doc/io.cloudevents/cloudevents-http-basic)
-   [cloudevents-http-restful-ws](https://www.javadoc.io/doc/io.cloudevents/cloudevents-http-restful-ws)
-   [cloudevents-http-vertx](https://www.javadoc.io/doc/io.cloudevents/cloudevents-http-vertx)
-   [cloudevents-kafka](https://www.javadoc.io/doc/io.cloudevents/cloudevents-kafka)

You can check out the examples in the [examples](examples) directory.

## Used By

| [Occurrent](https://occurrent.org)                                                                                                                                                                                                              | [Knative Eventing](https://github.com/knative-sandbox/eventing-kafka-broker)                                                                                   |
| ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| <a href="https://occurrent.org"><img src="https://raw.githubusercontent.com/johanhaleby/occurrent/master/occurrent-logo-196x196.png" width="98" height="98" alt="Occurrent" title="Occurrent - Event Sourcing Utilities for the JVM"></img></a> | <a href="https://github.com/knative-sandbox/eventing-kafka-broker"><img src="https://cloudevents.io/img/logos/integrations/knative.png" height="98"></img></a> |

## Community

-   There are bi-weekly calls immediately following the
    [Serverless/CloudEvents call](https://github.com/cloudevents/spec#meeting-time)
    at 9am PT (US Pacific). Which means they will typically start at 10am PT,
    but if the other call ends early then the SDK call will start early as well.
    See the
    [CloudEvents meeting minutes](https://docs.google.com/document/d/1OVF68rpuPK5shIHILK9JOqlZBbfe91RNzQ7u_P7YCDE/edit#)
    to determine which week will have the call.
-   Slack: #cloudeventssdk channel under
    [CNCF's Slack workspace](https://slack.cncf.io/).
-   Email: https://lists.cncf.io/g/cncf-cloudevents-sdk
-   Contact for additional information: Francesco Guardiani (`@slinkydeveloper`
    on slack), Fabio José (`@fabiojose` on slack).

Each SDK may have its own unique processes, tooling and guidelines, common
governance related material can be found in the
[CloudEvents `community`](https://github.com/cloudevents/spec/tree/master/community)
directory. In particular, in there you will find information concerning how SDK
projects are
[managed](https://github.com/cloudevents/spec/blob/master/community/SDK-GOVERNANCE.md),
[guidelines](https://github.com/cloudevents/spec/blob/master/community/SDK-maintainer-guidelines.md)
for how PR reviews and approval, and our
[Code of Conduct](https://github.com/cloudevents/spec/blob/master/community/GOVERNANCE.md#additional-information)
information.
