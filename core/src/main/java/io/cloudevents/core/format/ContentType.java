/*
 * Copyright 2018-Present The CloudEvents Authors
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package io.cloudevents.core.format;

import io.cloudevents.CloudEvent;
import io.cloudevents.CloudEventData;
import io.cloudevents.rw.CloudEventDataMapper;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.Set;

/**
 * <p>A construct that aggregates a two-part identifier of file formats and format contents transmitted on the Internet.
 *
 * <p>The two parts of a {@code ContentType} are its <em>type</em> and a <em>subtype</em>; separated by a forward slash ({@code /}).
 *
 * <p>The constants enumerated by {@code ContentType} correspond <em>only</em> to the specialized formats supported by the Java™ SDK for CloudEvents.
 *
 * @see io.cloudevents.core.format.EventFormat
 */
@ParametersAreNonnullByDefault
public enum ContentType {

    /**
     * Content type associated with the CSV event format
     */
    CSV("application/cloudevents+csv"),
    /**
     * Content type associated with the JSON event format
     */
    JSON("application/cloudevents+json"),
    /**
     * The content type for transports sending cloudevents in the protocol buffer format.
     */
    PROTO("application/cloudevents+protobuf"),
    /**
     * The content type for transports sending cloudevents in XML format.
     */
    XML("application/cloudevents+xml");

    private String value;

    private ContentType(String value) { this.value = value; }

    /**
     * Return a string consisting of the slash-delimited ({@code /}) two-part identifier for this {@code enum} constant.
     */
    public String value() { return value; }

    /**
     * Return a string consisting of the slash-delimited ({@code /}) two-part identifier for this {@code enum} constant.
     */
    @Override
    public String toString() { return value(); }

}
