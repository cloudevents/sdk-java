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

package io.cloudevents.core.builder;

import io.cloudevents.CloudEvent;
import io.cloudevents.Extension;
import io.cloudevents.SpecVersion;
import io.cloudevents.visitor.CloudEventVisitor;

import javax.annotation.Nonnull;
import java.net.URI;
import java.time.ZonedDateTime;

/**
 * Builder interface to build a {@link CloudEvent}.
 */
public interface CloudEventBuilder extends CloudEventVisitor<CloudEvent> {

    /**
     * Set the {@code id} of the event
     *
     * @param id id of the event
     * @return self
     */
    CloudEventBuilder withId(String id);

    /**
     * Set the {@code source} of the event
     *
     * @param source source of the event
     * @return self
     */
    CloudEventBuilder withSource(URI source);

    /**
     * Set the {@code type} of the event
     *
     * @param type type of the event
     * @return self
     */
    CloudEventBuilder withType(String type);

    /**
     * Set the {@code dataschema} of the event. For CloudEvent v0.3, this will configure the {@code schemaurl} attribute.
     *
     * @param dataSchema dataschema of the event
     * @return self
     */
    CloudEventBuilder withDataSchema(URI dataSchema);

    /**
     * Set the {@code datacontenttype} of the event
     *
     * @param dataContentType datacontenttype of the event
     * @return self
     */
    CloudEventBuilder withDataContentType(String dataContentType);

    /**
     * Set the {@code subject} of the event
     *
     * @param subject subject of the event
     * @return self
     */
    CloudEventBuilder withSubject(String subject);

    /**
     * Set the {@code time} of the event
     *
     * @param time time of the event
     * @return self
     */
    CloudEventBuilder withTime(ZonedDateTime time);

    /**
     * Set the {@code data} of the event
     *
     * @param data data of the event
     * @return self
     */
    CloudEventBuilder withData(byte[] data);

    /**
     * Set the {@code datacontenttype} and {@code data} of the event
     *
     * @param dataContentType datacontenttype of the event
     * @param data            data of the event
     * @return self
     */
    CloudEventBuilder withData(String dataContentType, byte[] data);

    /**
     * Set the {@code datacontenttype}, {@code dataschema} and {@code data} of the event
     *
     * @param dataContentType datacontenttype of the event
     * @param dataSchema      dataschema of the event
     * @param data            data of the event
     * @return self
     */
    CloudEventBuilder withData(String dataContentType, URI dataSchema, byte[] data);

    /**
     * Set an extension with provided key and string value
     *
     * @param key   key of the extension attribute
     * @param value value of the extension attribute
     * @return self
     */
    CloudEventBuilder withExtension(String key, String value);

    /**
     * Set an extension with provided key and numeric value
     *
     * @param key   key of the extension attribute
     * @param value value of the extension attribute
     * @return self
     */
    CloudEventBuilder withExtension(String key, Number value);

    /**
     * Set an extension with provided key and boolean value
     *
     * @param key   key of the extension attribute
     * @param value value of the extension attribute
     * @return self
     */
    CloudEventBuilder withExtension(String key, boolean value);

    /**
     * Add to the builder all the extension key/values of the provided extension
     *
     * @param extension materialized extension to set in the event
     * @return self
     */
    CloudEventBuilder withExtension(Extension extension);

    /**
     * Build the event
     *
     * @return the built event
     * @throws IllegalStateException if a required attribute is not configured
     */
    CloudEvent build() throws IllegalStateException;

    /**
     * @return a new CloudEvent v1 builder
     */
    static io.cloudevents.core.v1.CloudEventBuilder v1() {
        return new io.cloudevents.core.v1.CloudEventBuilder();
    }

    /**
     * @param event event to bootstrap the builder
     * @return a new CloudEvent v1 builder filled with content of {@code event}
     */
    static io.cloudevents.core.v1.CloudEventBuilder v1(CloudEvent event) {
        return new io.cloudevents.core.v1.CloudEventBuilder(event);
    }

    /**
     * @return a new CloudEvent v0.3 builder
     */
    static io.cloudevents.core.v03.CloudEventBuilder v03() {
        return new io.cloudevents.core.v03.CloudEventBuilder();
    }

    /**
     * @param event event to bootstrap the builder
     * @return a new CloudEvent v0.3 builder filled with content of {@code event}
     */
    static io.cloudevents.core.v03.CloudEventBuilder v03(CloudEvent event) {
        return new io.cloudevents.core.v03.CloudEventBuilder(event);
    }

    /**
     * Create a new builder for the specified {@link SpecVersion}
     *
     * @param version version to use for the new builder
     * @return a new builder
     */
    static CloudEventBuilder fromSpecVersion(@Nonnull SpecVersion version) {
        switch (version) {
            case V1:
                return CloudEventBuilder.v1();
            case V03:
                return CloudEventBuilder.v03();
        }
        throw new IllegalStateException(
            "The provided spec version doesn't exist. Please make sure your io.cloudevents deps versions are aligned."
        );
    }

}
