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



	/**
     * The in-memory format for distributed tracing.
     * <br/>
     * Details <a href="https://github.com/cloudevents/spec/blob/v0.2/extensions/distributed-tracing.md#in-memory-formats">here</a>
     * @author fabiojose
     *
     */
    public static class InMemory implements ExtensionFormat {
    	
    	public static final String IN_MEMORY_KEY = "distributedTracing";
    	
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
