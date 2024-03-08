/*
 * Copyright 2020-Present The CloudEvents Authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.cloudevents.spring.amqp;

/**
 * Constants used throughout the Spring AMQP binding for cloud events.
 */
public class CloudEventsHeaders {

    /**
     * CloudEvent attributes MUST be prefixed with either "cloudEvents_" or "cloudEvents:" for use in the application-properties section.
     *
     * @see <a href="https://github.com/cloudevents/spec/blob/main/cloudevents/bindings/amqp-protocol-binding.md#3131-amqp-application-property-names">
     * AMQP Protocol Binding for CloudEvents</a>
     */
    public static final String CE_PREFIX = "cloudEvents_";
    /**
     * CloudEvents AMQP consumers SHOULD understand the "cloudEvents" prefix with both the '_' and the ':' separators as permitted within the constraints of the client model.
     *
     * @see <a href="https://github.com/cloudevents/spec/blob/main/cloudevents/bindings/amqp-protocol-binding.md#3131-amqp-application-property-names">
     * AMQP Protocol Binding for CloudEvents</a>
     */
    public static final String ALT_CE_PREFIX = "cloudEvents:";
    /**
     * The spec version header name.
     */
    public static final String SPEC_VERSION = CE_PREFIX + "specversion";
    /**
     * The alternative spec version header name.
     */
    public static final String ALT_SPEC_VERSION = ALT_CE_PREFIX + "specversion";
    /**
     * The data content-type header name.
     */
    public static final String CONTENT_TYPE = CE_PREFIX + "datacontenttype";

    /**
     * The alternative data content-type header name.
     */
    public static final String ALT_CONTENT_TYPE = ALT_CE_PREFIX + "datacontenttype";
}
