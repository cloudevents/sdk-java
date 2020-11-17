## Spring Support

### Introduction

This module provides classes and interfaces that can be used by [Spring frameworks](https://spring.io/) and integrations to assist with Cloud Event processing. 

Given that Spring defines [Message](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/messaging/Message.html) abstraction, 
which perfectly maps to the structure defined by Cloud Events specification, one may say Cloud Events are already supported by any Spring framework that 
relies on `Message`. So this modules provides several utilities and strategies to simplify working with Cloud Events in the context of Spring 
frameworks and integrations (see individual component's javadocs for more details).

Please see individual samples in `examples/spring` directory of this SDK for more details.
