package io.cloudevents.http.springboot.web.helper;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.SerializationUtils;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.Map;

public class CloudEventsHelper {

    public static final String CE_ID = "Ce-Id";
    public static final String CE_TYPE = "Ce-Type";
    public static final String CE_SOURCE = "Ce-Source";
    public static final String CE_SPECVERSION = "Ce-Specversion";
    public static final String CE_TIME = "Ce-Time";

    public static final String APPLICATION_JSON = "application/json";
    public static final String CONTENT_TYPE = "Content-Type";


    public static CloudEvent parseFromRequest(Map<String, String> headers, Object body) throws IllegalStateException {
        if (headers.get(CE_ID) == null || (headers.get(CE_SOURCE) == null || headers.get(CE_TYPE) == null)) {
            throw new IllegalStateException("Cloud Event required fields are not present.");
        }

        return CloudEventBuilder.v03()
                .withId(headers.get(CE_ID))
                .withType(headers.get(CE_TYPE))
                .withSource((headers.get(CE_SOURCE) != null) ? URI.create(headers.get(CE_SOURCE)) : null)
                .withTime((headers.get(CE_TIME) != null) ? ZonedDateTime.parse(headers.get(CE_TIME)) : null)
                .withData(SerializationUtils.serialize(body))
                .withDataContentType((headers.get(CONTENT_TYPE) != null) ? headers.get(CONTENT_TYPE) : APPLICATION_JSON)
                .build();
    }


    public static ResponseEntity<String> createPostCloudEvent(RestTemplate restTemplate, String host, CloudEvent cloudEvent) {

        HttpHeaders headers = new HttpHeaders();
        headers.add(CE_ID, cloudEvent.getId());
        headers.add(CE_SPECVERSION, cloudEvent.getSpecVersion().name());
        headers.add(CONTENT_TYPE, APPLICATION_JSON);
        headers.add(CE_TYPE, cloudEvent.getType());
        headers.add(CE_TIME, cloudEvent.getTime().toString());
        headers.add(CE_SOURCE, cloudEvent.getSource().toString());

        for(String key : cloudEvent.getExtensionNames()) {
            headers.add(key, cloudEvent.getExtension(key).toString());
        }

        HttpEntity<String> request = new HttpEntity<String>(SerializationUtils.deserialize(cloudEvent.getData()).toString(), headers);

        return restTemplate.postForEntity(host, request, String.class);
    }


}
