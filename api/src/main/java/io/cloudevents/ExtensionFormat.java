package io.cloudevents.v02;

import io.cloudevents.Extension;

/**
 *  See details about in-memory format
 * <a href="https://github.com/cloudevents/spec/blob/v0.2/documented-extensions.md#usage">here</a>
 * 
 * @author fabiojose
 * 
 */
public interface ExtensionFormat {
	/**
	 * The in-memory format key
	 */
	String getKey();
	
	/**
	 * The extension implementation
	 */
	Extension getExtension();
}
