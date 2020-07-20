package io.cloudevents.http.springboot.webflux.helper;

import io.cloudevents.CloudEvent;
import io.cloudevents.Extension;
import io.cloudevents.core.builder.CloudEventBuilder;
import org.springframework.util.SerializationUtils;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.Map;

public class CloudEventsHelper {

    public static final String CE_ID = "Ce-Id";
    public static final String CE_TYPE = "Ce-Type";
    public static final String CE_SOURCE = "Ce-Source";
    public static final String CE_SPECVERSION = "Ce-Specversion";
    public static final String CE_TIME = "Ce-Time";
    public static final String CE_SUBJECT = "Ce-Subject";

    public static final String APPLICATION_JSON = "application/json";
    public static final String CONTENT_TYPE = "Content-Type";


    public static CloudEvent parseFromRequest(Map<String, String> headers, Object body) throws IllegalStateException {
        return parseFromRequestWithExtension(headers, body, null);
    }


    public static CloudEvent parseFromRequestWithExtension(Map<String, String> headers, Object body, Extension extension) {
        if (headers.get(CE_ID) == null || (headers.get(CE_SOURCE) == null || headers.get(CE_TYPE) == null)) {
            throw new IllegalStateException("Cloud Event required fields are not present.");
        }

        CloudEventBuilder builder = CloudEventBuilder.v03()
            .withId(headers.get(CE_ID))
            .withType(headers.get(CE_TYPE))
            .withSource((headers.get(CE_SOURCE) != null) ? URI.create(headers.get(CE_SOURCE)) : null)
            .withTime((headers.get(CE_TIME) != null) ? ZonedDateTime.parse(headers.get(CE_TIME)) : null)
            .withData(SerializationUtils.serialize(body))
            .withSubject(headers.get(CE_SUBJECT))
            .withDataContentType((headers.get(CONTENT_TYPE) != null) ? headers.get(CONTENT_TYPE) : APPLICATION_JSON);

        if (extension != null) {
            builder = builder.withExtension(extension);
        }
        return builder.build();
    }

    public static WebClient.ResponseSpec createPostCloudEvent(WebClient webClient, CloudEvent cloudEvent) {
        return createPostCloudEvent(webClient, "", cloudEvent);
    }

    public static WebClient.ResponseSpec createPostCloudEvent(WebClient webClient, String uriString, CloudEvent cloudEvent) {
        WebClient.RequestBodySpec uri = webClient.post().uri(uriString);
        WebClient.RequestHeadersSpec<?> headersSpec = uri.body(BodyInserters.fromValue(cloudEvent.getData()));
        WebClient.RequestHeadersSpec<?> header = headersSpec
            .header(CE_ID, cloudEvent.getId())
            .header(CE_SPECVERSION, cloudEvent.getSpecVersion().name())
            .header(CONTENT_TYPE, APPLICATION_JSON)
            .header(CE_TYPE, cloudEvent.getType())
            .header(CE_TIME, cloudEvent.getTime().toString())
            .header(CE_SOURCE, cloudEvent.getSource().toString())
            .header(CE_SUBJECT, cloudEvent.getSubject());


        //@TODO: improve extensions handling, at least now we will have a string version of the extension
        for (String key : cloudEvent.getExtensionNames()) {
            header.header(key, cloudEvent.getExtension(key).toString());
        }
        return header.retrieve();
    }


    //@TODO: create a print CLOUD EVENT helper

}
