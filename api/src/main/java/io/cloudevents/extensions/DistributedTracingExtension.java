package io.cloudevents.extensions;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.AbstractMap.SimpleEntry;

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
    	public static final String TRACE_PARENT_KEY = "traceparent";
    	public static final String TRACE_STATE_KEY = "tracestate";

    	private final InMemoryFormat memory;
    	private final Map<String, String> transport = new HashMap<>();
    	public Format(DistributedTracingExtension extension) {
    		Objects.requireNonNull(extension);
    		
    		memory = InMemoryFormat.of(IN_MEMORY_KEY, extension, 
    				DistributedTracingExtension.class);
    		
    		transport.put(TRACE_PARENT_KEY, extension.getTraceparent());
    		transport.put(TRACE_STATE_KEY, extension.getTracestate());
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
    
    /**
     * Unmarshals the {@link DistributedTracingExtension} based on map of extensions.
     * @param exts
     * @return
     */
    public static Optional<ExtensionFormat> unmarshall(
    		Map<String, String> exts) {
    	String traceparent = exts.get(Format.TRACE_PARENT_KEY);
		String tracestate  = exts.get(Format.TRACE_STATE_KEY);
		
		if(null!= traceparent && null!= tracestate) {
			DistributedTracingExtension dte = new DistributedTracingExtension();
			dte.setTraceparent(traceparent);
			dte.setTracestate(tracestate);
			
			InMemoryFormat inMemory = 
				InMemoryFormat.of(Format.IN_MEMORY_KEY, dte, 
						DistributedTracingExtension.class);
			
			return Optional.of(
				ExtensionFormat.of(inMemory, 
					new SimpleEntry<>(Format.TRACE_PARENT_KEY, traceparent),
					new SimpleEntry<>(Format.TRACE_STATE_KEY, tracestate))
			);
			
		}
		
		return Optional.empty();	
    }
}
