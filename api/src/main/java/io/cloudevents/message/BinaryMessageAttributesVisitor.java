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

package io.cloudevents.message;

import io.cloudevents.types.Time;

import java.net.URI;
import java.time.ZonedDateTime;

@FunctionalInterface
public interface BinaryMessageAttributesVisitor {

    /**
     * Set attribute with type {@link String}. This setter should not be invoked for specversion, because the built Visitor already
     * has the information through the {@link BinaryMessageVisitorFactory}
     *
     * @param name
     * @param value
     * @throws MessageVisitException
     */
    void setAttribute(String name, String value) throws MessageVisitException;

    /**
     * Set attribute with type {@link URI}.
     *
     * @param name
     * @param value
     * @throws MessageVisitException
     */
    default void setAttribute(String name, URI value) throws MessageVisitException {
        setAttribute(name, value.toString());
    }

    /**
     * Set attribute with type {@link ZonedDateTime} attribute.
     *
     * @param name
     * @param value
     * @throws MessageVisitException
     */
    default void setAttribute(String name, ZonedDateTime value) throws MessageVisitException {
        setAttribute(name, value.format(Time.RFC3339_DATE_FORMAT));
    }

}
