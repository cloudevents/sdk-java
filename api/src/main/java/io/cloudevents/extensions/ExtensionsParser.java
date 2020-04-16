package io.cloudevents.extensions;

import io.cloudevents.CloudEvent;
import io.cloudevents.Extension;

import java.util.HashMap;
import java.util.function.Supplier;

public class ExtensionsParser {

    private static class SingletonContainer {
        private final static ExtensionsParser INSTANCE = new ExtensionsParser();
    }

    public static ExtensionsParser getInstance() {
        return SingletonContainer.INSTANCE;
    }

    private HashMap<Class<?>, Supplier<Extension>> extensionFactories;

    private ExtensionsParser() {
        this.extensionFactories = new HashMap<>();
        registerExtension(DistributedTracingExtension.class, DistributedTracingExtension::new);
    }

    public <T extends Extension> void registerExtension(Class<T> extensionClass, Supplier<Extension> factory) {
        this.extensionFactories.put(extensionClass, factory);
    }

    public Extension parseExtension(Class<Extension> extensionClass, CloudEvent event) {
        Supplier<Extension> factory = extensionFactories.get(extensionClass);
        if (factory != null) {
            Extension ext = factory.get();
            ext.readFromEvent(event);
            return ext;
        }
        return null;
    }

}
