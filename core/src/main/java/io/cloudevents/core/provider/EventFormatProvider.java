/*
 * Copyright 2018-Present The CloudEvents Authors
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package io.cloudevents.core.provider;

import java.util.HashMap;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.StreamSupport;

import javax.annotation.ParametersAreNonnullByDefault;

import io.cloudevents.core.format.ContentType;
import io.cloudevents.core.format.EventFormat;
import io.cloudevents.lang.Nullable;

/**
 * Singleton holding the discovered {@link EventFormat} implementations through
 * {@link ServiceLoader}.
 * <p>
 * You can resolve an event format using
 * {@code EventFormatProvider.getInstance().resolveFormat(contentType)}.
 * <p>
 * You can programmatically add a new {@link EventFormat} implementation using
 * {@link #registerFormat(EventFormat)}.
 */
@ParametersAreNonnullByDefault
public final class EventFormatProvider {

	private static class SingletonContainer {
		private final static EventFormatProvider INSTANCE = new EventFormatProvider();
	}

	/**
	 * @return instance of {@link EventFormatProvider}
	 */
	public static EventFormatProvider getInstance() {
		return EventFormatProvider.SingletonContainer.INSTANCE;
	}

	private final HashMap<String, EventFormat> formats;

	private EventFormatProvider() {
		this.formats = new HashMap<>();

		StreamSupport.stream(ServiceLoader.load(EventFormat.class).spliterator(), false)
				.forEach(this::registerFormat);
	}

	/**
	 * Register a new {@link EventFormat} programmatically.
	 *
	 * @param format the new format to register
	 */
	public void registerFormat(EventFormat format) {
		for (String k : format.deserializableContentTypes()) {
			this.formats.put(k, format);
		}
	}

	/**
	 * Enumerate the supported content types.
	 * 
	 * @return an alphabetically sorted list of content types
	 */
	public Set<String> getContentTypes() {
		Set<String> types = new TreeSet<>();
		types.addAll(this.formats.keySet());
		return types;
	}

	/**
	 * Resolve an event format starting from the content type.
	 *
	 * @param contentType the content type to resolve the event format
	 * @return null if no format was found for the provided content type
	 */
	@Nullable
	public EventFormat resolveFormat(String contentType) {
		int i = contentType.indexOf(';');
		if (i != -1) {
			contentType = contentType.substring(0, i);
		}
		return this.formats.get(contentType);
	}

	/**
	 * Resolve an event format starting from the content type.
	 *
	 * @param contentType the content type to resolve the event format
	 * @return null if no format was found for the provided content type
	 */
	@Nullable
	public EventFormat resolveFormat(ContentType contentType) {
		return this.formats.get(contentType.value());
	}
}
