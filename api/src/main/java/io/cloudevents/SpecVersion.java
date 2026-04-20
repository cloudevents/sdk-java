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

import io.cloudevents.rw.CloudEventRWException;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Represents one of the supported CloudEvents specification versions by this library
 */
@ParametersAreNonnullByDefault
public enum SpecVersion {
    /**
     * @see <a href="https://github.com/cloudevents/spec/releases/tag/v0.3">CloudEvents release v0.3</a>
     */
    V03(
        "0.3",
        Arrays.asList("specversion", "id", "type", "source"),
        Arrays.asList("datacontenttype", "datacontentencoding", "schemaurl", "subject", "time")
    ),
    /**
     * @see <a href="https://github.com/cloudevents/spec/releases/tag/v1.0">CloudEvents release v1.0</a>
     */
    V1(
        "1.0",
        Arrays.asList("specversion", "id", "type", "source"),
        Arrays.asList("datacontenttype", "dataschema", "subject", "time")
    );

    private final String version;
    private final Set<String> mandatoryAttributes;
    private final Set<String> optionalAttributes;
    private final Set<String> allAttributes;

    SpecVersion(String version, Collection<String> mandatoryAttributes, Collection<String> optionalAttributes) {
        this.version = version;
        this.mandatoryAttributes = Collections.unmodifiableSet(new HashSet<>(mandatoryAttributes));
        this.optionalAttributes = Collections.unmodifiableSet(new HashSet<>(optionalAttributes));
        this.allAttributes = Collections.unmodifiableSet(
            Stream.concat(mandatoryAttributes.stream(), optionalAttributes.stream()).collect(Collectors.toSet())
        );
    }

    @Override
    public String toString() {
        return this.version;
    }

    /**
     * Parse a string as {@link SpecVersion}
     *
     * @param sv String representing the spec version
     * @return The parsed spec version
     * @throws CloudEventRWException When the spec version string is unrecognized
     */
    public static SpecVersion parse(String sv) {
        switch (sv) {
            case "0.3":
                return SpecVersion.V03;
            case "1.0":
                return SpecVersion.V1;
            default:
                throw CloudEventRWException.newInvalidSpecVersion(sv);
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
