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

import io.cloudevents.CloudEventExtensions;
import io.cloudevents.Extension;
import io.cloudevents.core.extensions.DatarefExtension;
import io.cloudevents.core.extensions.DistributedTracingExtension;
import io.cloudevents.lang.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.function.Supplier;

/**
 * Singleton to materialize CloudEvent extensions as POJOs.
 * <p>
 * You can materialize an {@link Extension} POJO with {@code ExtensionProvider.getInstance().parseExtension(DistributedTracingExtension.class, event)}.
 */
@ParametersAreNonnullByDefault
public final class ExtensionProvider {

    private static class SingletonContainer {
        private static final ExtensionProvider INSTANCE = new ExtensionProvider();
    }

    public static ExtensionProvider getInstance() {
        return SingletonContainer.INSTANCE;
    }

    private final HashMap<Class<?>, Supplier<?>> extensionFactories;

    // TODO SPI in future?
    private ExtensionProvider() {
        this.extensionFactories = new HashMap<>();
        registerExtension(DistributedTracingExtension.class, DistributedTracingExtension::new);
        registerExtension(DatarefExtension.class, DatarefExtension::new);
    }

    /**
     * Register a new extension type.
     *
     * @param extensionClass the class implementing {@link Extension}
     * @param factory the empty arguments factory
     * @param <T> the type of the extension
     */
    public <T extends Extension> void registerExtension(Class<T> extensionClass, Supplier<T> factory) {
        this.extensionFactories.put(extensionClass, factory);
    }

    /**
     * Parse an extension from the {@link CloudEventExtensions}, materializing the corresponding POJO.
     *
     * @param extensionClass  the class implementing {@link Extension}
     * @param eventExtensions the event extensions to read
     * @param <T>             the type of the extension
     */
    @SuppressWarnings("unchecked")
    @Nullable
    public <T extends Extension> T parseExtension(Class<T> extensionClass, CloudEventExtensions eventExtensions) {
        Supplier<?> factory = extensionFactories.get(extensionClass);
        if (factory != null) {
            Extension ext = (Extension) factory.get();
            ext.readFrom(eventExtensions);
            return (T) ext;
        }
        return null;
    }

}
