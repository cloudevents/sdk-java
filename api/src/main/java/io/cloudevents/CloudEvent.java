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

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.Map;

import javax.annotation.ParametersAreNonnullByDefault;

import io.cloudevents.lang.Nullable;

/**
 * An abstract event envelope
 *
 * @author fabiojose
 * @author slinkydeveloper
 */
@ParametersAreNonnullByDefault
public interface CloudEvent {

	/**
	 *
	 * @return the event data, effectively representing the payload of this event.
	 */
    @Nullable
    byte[] getData();

    /**
     * @return the version of the Cloud Events specification which this event uses.
     */
    SpecVersion getSpecVersion();

	/**
	 * @return  The identifier of this event. Producers MUST ensure that {@link #getSource()} + {@link #getId()} is unique for each distinct event.
	 */
	String getId();

	/**
	 * @return a value describing the type of event related to the originating occurrence.
	 */
	String getType();

	/**
	 * @return the context in which an event happened.
	 */
	URI getSource();

	/**
	 * @return the content type of data value. It MUST follows the <a href="https://tools.ietf.org/html/rfc2046">RFC2046</a>
	 */
	@Nullable
	String getDataContentType();

	/**
	 * @return the schema that data adheres to.
	 */
	@Nullable
	URI getDataSchema();

	/**
	 * @return the subject of the event in the context of the event producer (identified by {@link #getSource()}).
	 */
	@Nullable
	String getSubject();

	/**
	 * @return Timestamp of when the occurrence happened.
	 */
	@Nullable
	ZonedDateTime getTime();

	/**
	 *
	 * @return the event extensions. Extensions values could be String/Number/Boolean
	 */
    Map<String, Object> getExtensions();
}
