package io.cloudevents.types;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public final class Time {
    public static final DateTimeFormatter RFC3339_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");

    public static ZonedDateTime parseTime(String time) throws DateTimeParseException {
        return ZonedDateTime.parse(time, RFC3339_DATE_FORMAT);
    }
}
