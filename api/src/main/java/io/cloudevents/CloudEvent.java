/*
 * Copyright 2020 The CloudEvents Authors
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

import io.cloudevents.format.EventFormat;
import io.cloudevents.lang.Nullable;
import io.cloudevents.message.BinaryMessage;
import io.cloudevents.message.StructuredMessage;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;

/**
 * An abstract event envelope
 *
 * @author fabiojose
 * @author slinkydeveloper
 */
@ParametersAreNonnullByDefault
public interface CloudEvent {

    /**
     * The event context attributes
     */
    Attributes getAttributes();

    /**
     * The event data
     */
    @Nullable
    byte[] getData();

    /**
     * The event extensions
     * <p>
     * Extensions values could be String/Number/Boolean
     */
    Map<String, Object> getExtensions();

    CloudEvent toV03();

    CloudEvent toV1();

    BinaryMessage asBinaryMessage();

    StructuredMessage asStructuredMessage(EventFormat format);

    static io.cloudevents.v1.CloudEventBuilder buildV1() {
        return new io.cloudevents.v1.CloudEventBuilder();
    }

    static io.cloudevents.v1.CloudEventBuilder buildV1(CloudEvent event) {
        return new io.cloudevents.v1.CloudEventBuilder(event);
    }

    static io.cloudevents.v03.CloudEventBuilder buildV03() {
        return new io.cloudevents.v03.CloudEventBuilder();
    }

    static io.cloudevents.v03.CloudEventBuilder buildV03(CloudEvent event) {
        return new io.cloudevents.v03.CloudEventBuilder(event);
    }
}
