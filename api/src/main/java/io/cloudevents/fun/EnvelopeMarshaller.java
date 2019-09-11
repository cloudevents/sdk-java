package io.cloudevents.fun;

import io.cloudevents.Attributes;
import io.cloudevents.CloudEvent;

/**
 * 
 * @author fabiojose
 *
 */
@FunctionalInterface
public interface EnvelopeMarshaller<A extends Attributes, T, P> {

	/**
	 * Marshals a CloudEvent instance into the wire format
	 * @param event The CloudEvents instance
	 * @return the wire format
	 */
	P marshal(CloudEvent<A, T> event);
}
