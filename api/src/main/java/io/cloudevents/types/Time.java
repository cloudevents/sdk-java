package io.cloudevents.types;

import java.time.format.DateTimeFormatter;

public final class Time {
    public static final DateTimeFormatter RFC3339_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");
}
