package io.cloudevents.extensions;

import io.cloudevents.Extension;
import io.cloudevents.v02.ExtensionFormat;

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
    
    /**
     * The in-memory format for distributed tracing.
     * <br/>
     * Details <a href="https://github.com/cloudevents/spec/blob/v0.2/extensions/distributed-tracing.md#in-memory-formats">here</a>
     * @author fabiojose
     *
     */
    public static class InMemory implements ExtensionFormat {
    	
    	private static final String IN_MEMORY_KEY = "distributedTracing";
    	
    	private final Extension extension;
    	public InMemory(DistributedTracingExtension extension) {
    		this.extension = extension;
    	}

		@Override
		public String getKey() {
			return IN_MEMORY_KEY;
		}

		@Override
		public Extension getExtension() {
			return extension;
		}
    	
    }
}
