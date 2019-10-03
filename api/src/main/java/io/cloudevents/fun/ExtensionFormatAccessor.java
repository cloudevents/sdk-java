package io.cloudevents.fun;

import java.util.Collection;

import io.cloudevents.Attributes;
import io.cloudevents.CloudEvent;
import io.cloudevents.extensions.ExtensionFormat;

/**
 * 
 * @author fabiojose
 *
 */
@FunctionalInterface
public interface ExtensionFormatAccessor<A extends Attributes, T> {

	/**
	 * To get access to the internal collection of {@link ExtensionFormat} inside
	 * the {@link CloudEvent} implementation
	 * 
	 * @param cloudEvent
	 * @return
	 */
	Collection<ExtensionFormat> extensionsOf(CloudEvent<A, T> cloudEvent);
}
