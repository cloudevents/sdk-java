package io.cloudevents.v1;

import java.util.Collection;
import java.util.Objects;

import io.cloudevents.Attributes;
import io.cloudevents.CloudEvent;
import io.cloudevents.extensions.ExtensionFormat;
import io.cloudevents.fun.ExtensionFormatAccessor;

/**
 * 
 * @author fabiojose
 * @version 1.0
 */
public class Accessor {

	/**
	 * To get access the set of {@link ExtensionFormat} inside the 
	 * event.
	 * 
	 * <br>
	 * <br>
	 * This method follow the signature of 
	 * {@link ExtensionFormatAccessor#extensionsOf(CloudEvent)}
	 * 
	 * @param cloudEvent
	 * @throws IllegalArgumentException When argument is not an instance
	 * of {@link CloudEventImpl}
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
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
