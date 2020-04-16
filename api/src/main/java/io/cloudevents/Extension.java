package io.cloudevents;

public interface Extension {

    void readFromEvent(CloudEvent event);

    void writeToEvent(CloudEvent event);

}
