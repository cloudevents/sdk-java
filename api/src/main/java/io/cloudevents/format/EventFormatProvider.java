package io.cloudevents.format;

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

    private HashMap<String, EventFormat> formats;

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
