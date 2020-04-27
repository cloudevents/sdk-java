package io.cloudevents.kafka;

import org.apache.kafka.common.header.internals.RecordHeader;
import org.apache.kafka.common.header.internals.RecordHeaders;

public class KafkaUtils {

    static RecordHeaders kafkaHeaders(RecordHeader... headers) {
        RecordHeaders hs = new RecordHeaders();
        for (RecordHeader h : headers) {
            hs.add(h);
        }
        return hs;
    }

    static RecordHeader header(String key, String value) {
        return new RecordHeader(key, value.getBytes());
    }

}
