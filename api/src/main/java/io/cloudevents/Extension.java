package io.cloudevents;

import java.util.Map;

public interface Extension {

    void readFromEvent(CloudEvent event);

    Map<String, Object> asMap();

}
