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

package io.cloudevents.core.extensions;

import io.cloudevents.CloudEvent;
import io.cloudevents.Extension;

import java.util.HashMap;
import java.util.function.Supplier;

public final class ExtensionsParser {

    private static class SingletonContainer {
        private final static ExtensionsParser INSTANCE = new ExtensionsParser();
    }

    public static ExtensionsParser getInstance() {
        return SingletonContainer.INSTANCE;
    }

    private HashMap<Class<?>, Supplier<Extension>> extensionFactories;


    // TODO SPI in future?
    private ExtensionsParser() {
        this.extensionFactories = new HashMap<>();
        registerExtension(DistributedTracingExtension.class, DistributedTracingExtension::new);
    }

    public <T extends Extension> void registerExtension(Class<T> extensionClass, Supplier<Extension> factory) {
        this.extensionFactories.put(extensionClass, factory);
    }

    @SuppressWarnings("unchecked")
    public <T extends Extension> T parseExtension(Class<T> extensionClass, CloudEvent event) {
        Supplier<Extension> factory = extensionFactories.get(extensionClass);
        if (factory != null) {
            Extension ext = factory.get();
            ext.readFrom(event);
            return (T) ext;
        }
        return null;
    }

}
