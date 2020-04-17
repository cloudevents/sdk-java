/**
 * Copyright 2019 The CloudEvents Authors
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
 */
package io.cloudevents;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Map;
import java.util.Optional;

/**
 * An abstract event envelope
 * @author fabiojose
 * @author slinkydeveloper
 */
public interface CloudEvent {

    /**
     * The event context attributes
     */
    Attributes getAttributes();

    /**
     * The event data
     */
    Optional<String> getDataAsString() throws DataConversionException;

    /**
     * The event data
     */
    Optional<byte[]> getDataAsBytes() throws DataConversionException;

    /**
     * The event data
     */
    Optional<JsonNode> getDataAsJson() throws DataConversionException;

    /**
     * The event extensions
     *
     * Extensions values could be String/Number/Boolean
     */
    Map<String, Object> getExtensions();

    /**
     * Write an extension into this cloud event
     * @param e
     */
    default void writeExtension(Extension e) {
        e.writeToEvent(this);
    }

    static io.cloudevents.v1.CloudEventBuilder build() {
        return buildV1();
    }

    static io.cloudevents.v1.CloudEventBuilder buildV1() {
        return new io.cloudevents.v1.CloudEventBuilder();
    }

    static io.cloudevents.v03.CloudEventBuilder buildV03() {
        return new io.cloudevents.v03.CloudEventBuilder();
    }
}
