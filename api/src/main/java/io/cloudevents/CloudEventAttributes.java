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

package io.cloudevents;

import io.cloudevents.lang.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.net.URI;
import java.time.ZonedDateTime;

/**
 * The marker interface for CloudEvents attributes
 *
 * @author fabiojose
 */
@ParametersAreNonnullByDefault
public interface CloudEventAttributes {

    /**
     * @return The version of the CloudEvents specification which the event uses
     */
    SpecVersion getSpecVersion();

    /**
     * @return Identifies the event. Producers MUST ensure that source + id is unique for each distinct event
     */
    String getId();

    /**
     * @return A value describing the type of event related to the originating occurrence.
     */
    String getType();

    /**
     * @return The context in which an event happened.
     */
    URI getSource();

    /**
     * A common way to get the media type of CloudEvents 'data';
     *
     * @return If has a value, it MUST follows the <a href="https://tools.ietf.org/html/rfc2046">RFC2046</a>
     */
    @Nullable
    String getDataContentType();

    /**
     * @return Return the URI specifying the location of the schema for the provided data
     */
    @Nullable
    URI getDataSchema();

    /**
     * @return <a href="https://github.com/cloudevents/spec/blob/v1.0/spec.md#subject">Subject</a> of the event
     */
    @Nullable
    String getSubject();

    /**
     * @return <a href="https://github.com/cloudevents/spec/blob/v1.0/spec.md#time">Timestamp</a> of when the occurrence happened
     */
    @Nullable
    ZonedDateTime getTime();

}
