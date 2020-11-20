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

package io.cloudevents.core.extensions;

import io.cloudevents.CloudEventExtensions;
import io.cloudevents.Extension;
import io.cloudevents.core.extensions.impl.ExtensionUtils;
import java.net.URI;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * This extension supports the "Claim Check Pattern". It allows to specify a reference to a location where the event payload is stored.
 * @see <a href=https://github.com/cloudevents/spec/blob/v1.0/extensions/dataref.md>https://github.com/cloudevents/spec/blob/v1.0/extensions/dataref.md</a>
 */
public final class DatarefExtension implements Extension {

    public static final String DATAREF = "dataref";
    private static final Set<String> KEY_SET = Collections.unmodifiableSet(new HashSet<>(Collections.singletonList(DATAREF)));

    private URI dataref;

    public URI getDataref() {
        return dataref;
    }

    public void setDataref(URI dataref) {
        this.dataref = dataref;
    }

    @Override
    public void readFrom(CloudEventExtensions extensions) {
        Object value = extensions.getExtension(DATAREF);
        if (value != null) {
            this.dataref = URI.create(value.toString());
        }
    }

    @Override
    public Object getValue(String key) {
        if (DATAREF.equals(key)) {
            return this.dataref.toString();
        }
        throw ExtensionUtils.generateInvalidKeyException(this.getClass().getSimpleName(), key);
    }

    @Override
    public Set<String> getKeys() {
        return KEY_SET;
    }

    @Override
    public String toString() {
        return "DatarefExtension{" +
            "dataref='" + dataref + '\'' +
            '}';
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        return prime + ((dataref == null) ? 0 : dataref.hashCode());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        DatarefExtension other = (DatarefExtension) obj;
        if (dataref == null) {
            return other.dataref == null;
        } else {
            return dataref.equals(other.dataref);
        }
    }
}
