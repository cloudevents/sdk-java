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
import io.cloudevents.CloudEventExtension;
import io.cloudevents.core.extensions.impl.ExtensionUtils;

import java.util.*;

/**
 * This extension embeds context from Distributed Tracing so that distributed systems can include traces that span an event-driven system.
 *
 * @see <a href="https://github.com/cloudevents/spec/blob/main/extensions/distributed-tracing.md">https://github.com/cloudevents/spec/blob/main/extensions/distributed-tracing.md</a>
 */
public final class DistributedTracingExtension implements CloudEventExtension {

    /**
     * The key of the {@code traceparent} extension
     */
    public static final String TRACEPARENT = "traceparent";

    /**
     * The key of the {@code tracestate} extension
     */
    public static final String TRACESTATE = "tracestate";

    private static final Set<String> KEY_SET = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(TRACEPARENT, TRACESTATE)));

    private String traceparent;
    private String tracestate;

    /**
     * @return the {@code traceparent} contained in this extension.
     */
    public String getTraceparent() {
        return traceparent;
    }

    /**
     * @param traceparent the string to set as {@code traceparent}.
     */
    public void setTraceparent(String traceparent) {
        this.traceparent = traceparent;
    }

    /**
     * @return the {@code tracestate} contained in this extension.
     */
    public String getTracestate() {
        return tracestate;
    }

    /**
     * @param tracestate the string to set as {@code tracestate}.
     */
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
    public Object getValue(String key) {
        switch (key) {
            case TRACEPARENT:
                return this.traceparent;
            case TRACESTATE:
                return this.tracestate;
        }
        throw ExtensionUtils.generateInvalidKeyException(this.getClass(), key);
    }

    @Override
    public Set<String> getKeys() {
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DistributedTracingExtension that = (DistributedTracingExtension) o;
        return Objects.equals(getTraceparent(), that.getTraceparent()) &&
            Objects.equals(getTracestate(), that.getTracestate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTraceparent(), getTracestate());
    }
}
