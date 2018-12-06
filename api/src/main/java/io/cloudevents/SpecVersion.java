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
package io.cloudevents;

import java.util.HashMap;
import java.util.Map;

public enum SpecVersion {

    V_01("0.1"),
    V_02("0.2"),
    DEFAULT(V_02.toString());

    private final String version;

    SpecVersion(final String version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return version;
    }

    public String version() {
        return version;
    }

    public static SpecVersion fromVersion(final String version) {
        if (version == null)
            return null;

        final SpecVersion specVersion= VERSION_TO_SPEC.get(version);

        if (specVersion == null)
            throw new IllegalArgumentException();

        return specVersion;
    }

    private static final Map<String, SpecVersion> VERSION_TO_SPEC =
            new HashMap<>();

    static
    {
        SpecVersion[] instances = SpecVersion.class.getEnumConstants();

        for (int i = 0; i < instances.length; i++)
        {
            VERSION_TO_SPEC.put(instances[i].toString(), instances[i]);
        }
    }


}
