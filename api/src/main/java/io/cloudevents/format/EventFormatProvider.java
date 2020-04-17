package io.cloudevents.format;

import io.cloudevents.format.json.JsonFormat;

import java.util.HashMap;

public final class EventFormatProvider {

    private static class SingletonContainer {
        private final static EventFormatProvider INSTANCE = new EventFormatProvider();
    }

    public static EventFormatProvider getInstance() {
        return EventFormatProvider.SingletonContainer.INSTANCE;
    }

    private HashMap<String, EventFormat> formats;

    //TODO register stuff with SPI
    private EventFormatProvider() {
        registerFormat(JsonFormat.getInstance());
    }

    public void registerFormat(EventFormat format) {
        for (String k: format.supportedContentTypes()) {
            this.formats.put(k, format);
        }
    }

    public EventFormat resolveFormat(String key) {
        return this.formats.get(key);
    }

}
