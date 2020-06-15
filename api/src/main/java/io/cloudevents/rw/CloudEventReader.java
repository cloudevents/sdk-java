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

package io.cloudevents.rw;

/**
 * Represents an object that can be read as CloudEvent
 */
public interface CloudEventReader {

    /**
     * Visit self using the provided visitor factory
     *
     * @param visitorFactory a factory that generates a visitor starting from the SpecVersion of the event
     * @throws CloudEventRWException if something went wrong during the visit.
     */
    <V extends CloudEventWriter<R>, R> R read(CloudEventWriterFactory<V, R> visitorFactory) throws CloudEventRWException;

    /**
     * Visit self attributes using the provided visitor
     *
     * @param visitor Attributes visitor
     * @throws CloudEventRWException if something went wrong during the visit.
     */
    void readAttributes(CloudEventAttributesWriter visitor) throws CloudEventRWException;

    /**
     * Visit self extensions using the provided visitor
     *
     * @param visitor Extensions visitor
     * @throws CloudEventRWException if something went wrong during the visit.
     */
    void readExtensions(CloudEventExtensionsWriter visitor) throws CloudEventRWException;

}
