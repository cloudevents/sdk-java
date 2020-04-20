/**
 * Copyright 2019 The CloudEvents Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.cloudevents.v03;

import io.cloudevents.CloudEvent;
import io.cloudevents.SpecVersion;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author fabiojose
 */
public class CloudEventBuilderTest {

    @ParameterizedTest()
    @MethodSource("io.cloudevents.test.Data#v03Events")
    void testCopyWithBuilder(CloudEvent event) {
        assertThat(CloudEvent.buildV03(event).build()).isEqualTo(event);
    }

    @ParameterizedTest()
    @MethodSource("io.cloudevents.test.Data#v03Events")
    void testToV1(CloudEvent event) {
        CloudEvent eventV1 = CloudEvent.buildV1(event).build();

        assertThat(eventV1.getAttributes().getSpecVersion())
            .isEqualTo(SpecVersion.V1);

        assertThat(eventV1).isEqualTo(event.toV1());
    }

}
