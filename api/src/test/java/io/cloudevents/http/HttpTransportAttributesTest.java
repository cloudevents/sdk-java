/**
 * Copyright 2018 The CloudEvents Authors
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
package io.cloudevents.http;

import io.cloudevents.SpecVersion;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class HttpTransportAttributesTest {

    @Test
    public void testVersion01Headers() {

        final HttpTransportAttributes v01 = HttpTransportAttributes.getHttpAttributesForSpec(SpecVersion.V_01);
        assertThat(v01.specVersionKey()).isEqualTo("ce-cloudEventsVersion");
        assertThat(v01.timeKey()).isEqualTo("ce-eventTime");
        assertThat(v01.idKey()).isEqualTo("ce-eventID");
        assertThat(v01.schemaUrlKey()).isEqualTo("ce-schemaURL");
        assertThat(v01.typeKey()).isEqualTo("ce-eventType");

        // non-changed between 01 / 02
        assertThat(v01.sourceKey()).isEqualTo("ce-source");
    }

    @Test
    public void testVersion02Headers() {

        final HttpTransportAttributes v02 = HttpTransportAttributes.getHttpAttributesForSpec(SpecVersion.V_02);
        assertThat(v02.specVersionKey()).isEqualTo("ce-specversion");
        assertThat(v02.timeKey()).isEqualTo("ce-time");
        assertThat(v02.idKey()).isEqualTo("ce-id");
        assertThat(v02.schemaUrlKey()).isEqualTo("ce-schemaurl");
        assertThat(v02.typeKey()).isEqualTo("ce-type");

        // non-changed between 01 / 02
        assertThat(v02.sourceKey()).isEqualTo("ce-source");
    }
}
