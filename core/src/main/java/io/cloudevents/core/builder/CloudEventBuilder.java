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
import io.cloudevents.SpecVersion;
import io.cloudevents.visitor.CloudEventVisitor;

import javax.annotation.Nonnull;

public interface CloudEventBuilder extends CloudEventVisitor<CloudEvent> {

    CloudEvent build();

    static io.cloudevents.core.v1.CloudEventBuilder v1() {
        return new io.cloudevents.core.v1.CloudEventBuilder();
    }

    static io.cloudevents.core.v1.CloudEventBuilder v1(CloudEvent event) {
        return new io.cloudevents.core.v1.CloudEventBuilder(event);
    }

    static io.cloudevents.core.v03.CloudEventBuilder v03() {
        return new io.cloudevents.core.v03.CloudEventBuilder();
    }

    static io.cloudevents.core.v03.CloudEventBuilder v03(CloudEvent event) {
        return new io.cloudevents.core.v03.CloudEventBuilder(event);
    }

    static CloudEventBuilder fromSpecVersion(@Nonnull SpecVersion version) {
        switch (version) {
            case V1:
                return CloudEventBuilder.v1();
            case V03:
                return CloudEventBuilder.v03();
        }
        return null;
    }

}
