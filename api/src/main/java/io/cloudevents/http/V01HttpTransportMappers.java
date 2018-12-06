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

public class V01HttpTransportMappers implements HttpTransportAttributes {

    public static final String SPEC_VERSION_KEY = "ce-cloudEventsVersion";

    @Override
    public String typeKey() {
        return "ce-eventType";
    }

    @Override
    public String specVersionKey() {
        return SPEC_VERSION_KEY;
    }

    @Override
    public String sourceKey() {
        return "ce-source";
    }

    @Override
    public String idKey() {
        return "ce-eventID";
    }

    @Override
    public String timeKey() {
        return "ce-eventTime";
    }

    @Override
    public String schemaUrlKey() {
        return "ce-schemaURL";
    }
}
