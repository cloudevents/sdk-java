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

/**
 * Interface representing an in memory read only representation of a CloudEvent,
 * as specified by the <a href="https://github.com/cloudevents/spec/blob/v1.0/spec.md">CloudEvents specification</a>.
 */
public interface CloudEvent extends CloudEventContext {

    /**
     * @return The event data, if any, otherwise {@code null}
     */
    @Nullable
    CloudEventData getData();
}
