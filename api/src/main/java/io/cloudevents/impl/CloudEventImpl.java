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

package io.cloudevents.impl;

import io.cloudevents.Attributes;
import io.cloudevents.CloudEvent;
import io.cloudevents.SpecVersion;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.*;

public final class CloudEventImpl implements CloudEvent {

    private final AttributesInternal attributes;
    private final byte[] data;
    private final Map<String, Object> extensions;

    public CloudEventImpl(Attributes attributes, byte[] data, Map<String, Object> extensions) {
        Objects.requireNonNull(attributes);
        this.attributes = (AttributesInternal) attributes;
        this.data = data;
        this.extensions = extensions != null ? extensions : new HashMap<>();
    }

    @Override
    public byte[] getData() {
        return this.data;
    }

    @Override
	public SpecVersion getSpecVersion() {
		return this.attributes.getSpecVersion();
	}

	@Override
	public String getId() {
		return this.attributes.getId();
	}

	@Override
	public String getType() {
		return this.attributes.getType();
	}

	@Override
	public URI getSource() {
		return this.attributes.getSource();
	}

	@Override
	public String getDataContentType() {
		return this.attributes.getDataContentType();
	}

	@Override
	public URI getDataSchema() {
		return this.attributes.getDataSchema();
	}

	@Override
	public String getSubject() {
		return this.attributes.getSubject();
	}

	@Override
	public ZonedDateTime getTime() {
		return this.attributes.getTime();
	}

    @Override
    public Map<String, Object> getExtensions() {
        return Collections.unmodifiableMap(extensions);
    }

    Attributes getAttributes() {
        return this.attributes;
    }

    public CloudEvent toV03() {
        return new CloudEventImpl(
            attributes.toV03(),
            data,
            extensions
        );
    }

    public CloudEvent toV1() {
        return new CloudEventImpl(
            attributes.toV1(),
            data,
            extensions
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CloudEventImpl that = (CloudEventImpl) o;
        return Objects.equals(attributes, that.attributes) &&
            Arrays.equals(data, that.data) &&
            Objects.equals(extensions, that.extensions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(attributes, data, extensions);
    }

    @Override
    public String toString() {
        return "CloudEvent{" +
            "attributes=" + attributes +
            ((this.data != null) ? ", data=" + new String(this.data, StandardCharsets.UTF_8) : "") +
            ", extensions=" + extensions +
            '}';
    }
}
