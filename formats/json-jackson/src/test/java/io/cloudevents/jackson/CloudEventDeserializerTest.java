/*
 * Copyright 2021 the original author
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

package io.cloudevents.jackson;

import static org.assertj.core.api.Assertions.assertThatCode;

import io.cloudevents.core.provider.EventFormatProvider;
import io.cloudevents.rw.CloudEventRWException;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class CloudEventDeserializerTest {
    @Test
    void throwOnInvalidSpecversion() {
        assertThatCode(() -> EventFormatProvider.getInstance().resolveFormat(JsonFormat.CONTENT_TYPE)
            .deserialize(("{\"specversion\":\"9000.1\"}").getBytes(StandardCharsets.UTF_8)))
            .hasMessageContaining(CloudEventRWException.newInvalidSpecVersion("9000.1").getMessage());
    }
}
