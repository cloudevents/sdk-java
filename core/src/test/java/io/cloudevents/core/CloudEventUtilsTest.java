package io.cloudevents.core;

import io.cloudevents.CloudEvent;
import io.cloudevents.CloudEventData;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.core.mock.MyCloudEventData;
import io.cloudevents.rw.CloudEventDataMapper;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

class CloudEventUtilsTest {

    @Test
    void mapDataWithNullData() {
        AtomicInteger i = new AtomicInteger();
        CloudEventDataMapper<CloudEventData> mapper = data -> {
            i.incrementAndGet();
            return data;
        };

        CloudEvent cloudEvent = CloudEventBuilder.v1()
            .withId("aaa")
            .withSource(URI.create("localhost"))
            .withType("bbb")
            .build();

        assertThat(CloudEventUtils.mapData(cloudEvent, mapper))
            .isNull();
        assertThat(i)
            .hasValue(0);
    }

    @Test
    void mapData() {
        AtomicInteger i = new AtomicInteger();
        CloudEventDataMapper<CloudEventData> mapper = data -> {
            i.incrementAndGet();
            return data;
        };

        MyCloudEventData data = new MyCloudEventData(10);

        CloudEvent cloudEvent = CloudEventBuilder.v1()
            .withId("aaa")
            .withSource(URI.create("localhost"))
            .withType("bbb")
            .withData(data)
            .build();

        assertThat(CloudEventUtils.mapData(cloudEvent, mapper))
            .isEqualTo(data);
        assertThat(i)
            .hasValue(1);
    }
}
