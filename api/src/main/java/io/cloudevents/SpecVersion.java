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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum SpecVersion {
    V03(
        "0.3",
        Collections.unmodifiableSet(new HashSet<>(Arrays.asList("specversion", "id", "type", "source"))),
        Collections.unmodifiableSet(new HashSet<>(Arrays.asList("datacontenttype", "datacontentencoding", "schemaurl", "subject", "time")))
    ),
    V1(
        "1.0",
        Collections.unmodifiableSet(new HashSet<>(Arrays.asList("specversion", "id", "type", "source"))),
        Collections.unmodifiableSet(new HashSet<>(Arrays.asList("datacontenttype", "dataschema", "subject", "time")))
    );

    private final String stringValue;
    private final Set<String> mandatoryAttributes;
    private final Set<String> optionalAttributes;
    private final Set<String> allAttributes;

    SpecVersion(String stringValue, Set<String> mandatoryAttributes, Set<String> optionalAttributes) {
        this.stringValue = stringValue;
        this.mandatoryAttributes = mandatoryAttributes;
        this.optionalAttributes = optionalAttributes;
        this.allAttributes = Collections.unmodifiableSet(
            Stream.concat(mandatoryAttributes.stream(), optionalAttributes.stream()).collect(Collectors.toSet())
        );
    }

    @Override
    public String toString() {
        return this.stringValue;
    }

    public static SpecVersion parse(String sv) {
        switch (sv) {
            case "0.3":
                return SpecVersion.V03;
            case "1.0":
                return SpecVersion.V1;
            default:
                throw new IllegalArgumentException("Unrecognized SpecVersion " + sv);
        }
    }

    /**
     * @return mandatory attributes of the spec version
     */
    public Set<String> getMandatoryAttributes() {
        return mandatoryAttributes;
    }

    /**
     * @return optional attributes of the spec version
     */
    public Set<String> getOptionalAttributes() {
        return optionalAttributes;
    }

    /**
     * @return all attributes for this spec
     */
    public Set<String> getAllAttributes() {
        return allAttributes;
    }
}
