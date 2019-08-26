package io.cloudevents.extensions;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DistributedTracingExtension {

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
    public static class Format implements ExtensionFormat {
    	
    	public static final String IN_MEMORY_KEY = "distributedTracing";

    	private final InMemoryFormat memory;
    	private final Map<String, String> transport = new HashMap<>();
    	public Format(DistributedTracingExtension extension) {
    		Objects.requireNonNull(extension);
    		
    		memory = InMemoryFormat.of(IN_MEMORY_KEY, extension, Object.class);
    		
    		transport.put("traceparent", extension.getTraceparent());
    		transport.put("tracestate", extension.getTracestate());
    	}
    	
		@Override
		public InMemoryFormat memory() {
			return memory;
		}
		
		@Override
		public Map<String, String> transport() {
			return transport;
		}

		
    	
    }
}
