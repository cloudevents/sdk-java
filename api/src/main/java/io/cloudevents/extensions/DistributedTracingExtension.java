package io.cloudevents.extensions;

import io.cloudevents.Extension;

public class DistributedTracingExtension implements Extension {

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
    public String toString() {
        return "DistributedTracingExtension{" +
                "traceparent='" + traceparent + '\'' +
                ", tracestate='" + tracestate + '\'' +
                '}';
    }
}
