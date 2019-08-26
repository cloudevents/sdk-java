package io.cloudevents.v02;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;

import io.cloudevents.Attributes;
import io.cloudevents.CloudEvent;
import io.cloudevents.extensions.ExtensionFormat;

/**
 * 
 * @author fabiojose
 *
 */
public final class Accessor {

	/**
	 * To get access the set of {@link ExtensionFormat} inside the 
	 * event.
	 * 
	 * @param cloudEvent
	 * @throws IllegalArgumentException When argument is not an instance of {@link CloudEventImpl}
	 */
	public static <A extends Attributes, T> Collection<ExtensionFormat> 
		extensionsOf(CloudEvent<A, T> cloudEvent) {
		Objects.requireNonNull(cloudEvent);
		
		if(cloudEvent instanceof CloudEventImpl) {
			CloudEventImpl impl = (CloudEventImpl)cloudEvent;
			return impl.getExtensionsFormats();
		}
		
		throw new IllegalArgumentException("Invalid instance type: " 
				+ cloudEvent.getClass());
	}
}
