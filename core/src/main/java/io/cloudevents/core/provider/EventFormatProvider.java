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

import io.cloudevents.core.format.EventFormat;

import java.util.HashMap;
import java.util.ServiceLoader;
import java.util.stream.StreamSupport;

public final class EventFormatProvider {

    private static class SingletonContainer {
        private final static EventFormatProvider INSTANCE = new EventFormatProvider();
    }

    public static EventFormatProvider getInstance() {
        return EventFormatProvider.SingletonContainer.INSTANCE;
    }

    private final HashMap<String, EventFormat> formats;

    private EventFormatProvider() {
        this.formats = new HashMap<>();

        StreamSupport.stream(
            ServiceLoader.load(EventFormat.class).spliterator(),
            false
        ).forEach(this::registerFormat);
    }

    public void registerFormat(EventFormat format) {
        for (String k : format.deserializableContentTypes()) {
            this.formats.put(k, format);
        }
    }

    public EventFormat resolveFormat(String key) {
        int i = key.indexOf(';');
        if (i != -1) {
            key = key.substring(0, i);
        }
        return this.formats.get(key);
    }

}
