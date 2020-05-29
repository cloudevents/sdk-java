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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class DistributedTracingExtension implements Extension {

    public static final String TRACEPARENT = "traceparent";
    public static final String TRACESTATE = "tracestate";
    private static final Set<String> KEY_SET = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(TRACEPARENT, TRACESTATE)));

    private String traceparent;
    private String tracestate;

    public String getTraceparent() {
        return traceparent;
    }

    public void setTraceparent(String traceparent) {
        this.traceparent = traceparent;
    }

    public String getTracestate() {
        return tracestate;
    }

    public void setTracestate(String tracestate) {
        this.tracestate = tracestate;
    }

    @Override
    public void readFrom(CloudEventExtensions extensions) {
        Object tp = extensions.getExtension(TRACEPARENT);
        if (tp != null) {
            this.traceparent = tp.toString();
        }
        Object ts = extensions.getExtension(TRACESTATE);
        if (ts != null) {
            this.tracestate = ts.toString();
        }
    }

    @Override
    public Object getExtension(String extensionName) {
        switch (extensionName) {
            case TRACEPARENT:
                return this.traceparent;
            case TRACESTATE:
                return this.tracestate;
        }
        return null;
    }

    @Override
    public Set<String> getExtensionNames() {
        return KEY_SET;
    }

    @Override
    public String toString() {
        return "DistributedTracingExtension{" +
            "traceparent='" + traceparent + '\'' +
            ", tracestate='" + tracestate + '\'' +
            '}';
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((traceparent == null) ? 0
            : traceparent.hashCode());
        result = prime * result + ((tracestate == null) ? 0
            : tracestate.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DistributedTracingExtension other = (DistributedTracingExtension) obj;
        if (traceparent == null) {
            if (other.traceparent != null)
                return false;
        } else if (!traceparent.equals(other.traceparent))
            return false;
        if (tracestate == null) {
            if (other.tracestate != null)
                return false;
        } else if (!tracestate.equals(other.tracestate))
            return false;
        return true;
    }
}
