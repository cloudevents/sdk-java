package io.cloudevents.http.vertx.impl;

import io.cloudevents.SpecVersion;
import io.vertx.core.http.HttpHeaders;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CloudEventsHeaders {

    public static final String CE_PREFIX = "ce-";

    public static final Map<String, CharSequence> ATTRIBUTES_TO_HEADERS = Stream.concat(
        Stream.concat(SpecVersion.V1.getMandatoryAttributes().stream(), SpecVersion.V1.getOptionalAttributes().stream()),
        Stream.concat(SpecVersion.V03.getMandatoryAttributes().stream(), SpecVersion.V03.getOptionalAttributes().stream())
    )
        .distinct()
        .collect(Collectors.toMap(Function.identity(), v -> {
            if (v.equals("datacontenttype")) {
                return HttpHeaders.CONTENT_TYPE;
            }
            return HttpHeaders.createOptimized(CE_PREFIX + v);
        }));

    public static final CharSequence SPEC_VERSION = ATTRIBUTES_TO_HEADERS.get("specversion");

}
