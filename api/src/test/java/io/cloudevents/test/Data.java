/*
 * Copyright 2018-Present The CloudEvents Authors
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package io.cloudevents.test;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.stream.Stream;

import io.cloudevents.CloudEvent;
import io.cloudevents.impl.CloudEventUtils;
import io.cloudevents.types.Time;

public class Data {

    public static final String ID = "1";
    public static final String TYPE = "mock.test";
    public static final URI SOURCE = URI.create("http://localhost/source");
    public static final String DATACONTENTTYPE_JSON = "application/json";
    public static final String DATACONTENTTYPE_XML = "application/xml";
    public static final String DATACONTENTTYPE_TEXT = "text/plain";
    public static final URI DATASCHEMA = URI.create("http://localhost/schema");
    public static final String SUBJECT = "sub";
    public static final ZonedDateTime TIME = Time.parseTime("2018-04-26T14:48:09+02:00");

    public static byte[] DATA_JSON_SERIALIZED = "{}".getBytes();
    public static byte[] DATA_XML_SERIALIZED = "<stuff></stuff>".getBytes();
    public static byte[] DATA_TEXT_SERIALIZED = "Hello World Lorena!".getBytes();

    public static final CloudEvent V1_MIN = CloudEventUtils.buildV1()
        .withId(ID)
        .withType(TYPE)
        .withSource(SOURCE)
        .build();

    public static final CloudEvent V1_WITH_JSON_DATA = CloudEventUtils.buildV1()
        .withId(ID)
        .withType(TYPE)
        .withSource(SOURCE)
        .withData(DATACONTENTTYPE_JSON, DATASCHEMA, DATA_JSON_SERIALIZED)
        .withSubject(SUBJECT)
        .withTime(TIME)
        .build();

    public static final CloudEvent V1_WITH_JSON_DATA_WITH_EXT = CloudEventUtils.buildV1()
        .withId(ID)
        .withType(TYPE)
        .withSource(SOURCE)
        .withData(DATACONTENTTYPE_JSON, DATASCHEMA, DATA_JSON_SERIALIZED)
        .withSubject(SUBJECT)
        .withTime(TIME)
        .withExtension("astring", "aaa")
        .withExtension("aboolean", true)
        .withExtension("anumber", 10)
        .build();

    public static final CloudEvent V1_WITH_JSON_DATA_WITH_EXT_STRING = CloudEventUtils.buildV1()
        .withId(ID)
        .withType(TYPE)
        .withSource(SOURCE)
        .withData(DATACONTENTTYPE_JSON, DATASCHEMA, DATA_JSON_SERIALIZED)
        .withSubject(SUBJECT)
        .withTime(TIME)
        .withExtension("astring", "aaa")
        .withExtension("aboolean", "true")
        .withExtension("anumber", "10")
        .build();

    public static final CloudEvent V1_WITH_XML_DATA = CloudEventUtils.buildV1()
        .withId(ID)
        .withType(TYPE)
        .withSource(SOURCE)
        .withData(DATACONTENTTYPE_XML, DATA_XML_SERIALIZED)
        .withSubject(SUBJECT)
        .withTime(TIME)
        .build();

    public static final CloudEvent V1_WITH_TEXT_DATA = CloudEventUtils.buildV1()
        .withId(ID)
        .withType(TYPE)
        .withSource(SOURCE)
        .withData(DATACONTENTTYPE_TEXT, DATA_TEXT_SERIALIZED)
        .withSubject(SUBJECT)
        .withTime(TIME)
        .build();

    public static final CloudEvent V03_MIN = CloudEventUtils.toV03(V1_MIN);
    public static final CloudEvent V03_WITH_JSON_DATA = CloudEventUtils.toV03(V1_WITH_JSON_DATA);
    public static final CloudEvent V03_WITH_JSON_DATA_WITH_EXT =  CloudEventUtils.toV03(V1_WITH_JSON_DATA_WITH_EXT);
    public static final CloudEvent V03_WITH_JSON_DATA_WITH_EXT_STRING = CloudEventUtils.toV03(V1_WITH_JSON_DATA_WITH_EXT_STRING);
    public static final CloudEvent V03_WITH_XML_DATA = CloudEventUtils.toV03(V1_WITH_XML_DATA);
    public static final CloudEvent V03_WITH_TEXT_DATA = CloudEventUtils.toV03(V1_WITH_TEXT_DATA);

    public static Stream<CloudEvent> allEvents() {
        return Stream.concat(v1Events(), v03Events());
    }

    public static Stream<CloudEvent> allEventsWithoutExtensions() {
        return Stream.concat(v1Events(), v03Events()).filter(e -> e.getExtensions().isEmpty());
    }

    public static Stream<CloudEvent> allEventsWithStringExtensions() {
        return Stream.concat(v1EventsWithStringExt(), v03EventsWithStringExt());
    }

    public static Stream<CloudEvent> v1Events() {
        return Stream.of(
            Data.V1_MIN,
            Data.V1_WITH_JSON_DATA,
            Data.V1_WITH_JSON_DATA_WITH_EXT,
            Data.V1_WITH_XML_DATA,
            Data.V1_WITH_TEXT_DATA
        );
    }

    public static Stream<CloudEvent> v03Events() {
        return Stream.of(
            Data.V03_MIN,
            Data.V03_WITH_JSON_DATA,
            Data.V03_WITH_JSON_DATA_WITH_EXT,
            Data.V03_WITH_XML_DATA,
            Data.V03_WITH_TEXT_DATA
        );
    }

    public static Stream<CloudEvent> v1EventsWithStringExt() {
        return v1Events().map(ce -> {
            io.cloudevents.v1.CloudEventBuilder builder = CloudEventUtils.buildV1(ce);
            ce.getExtensions().forEach((k, v) -> builder.withExtension(k, v.toString()));
            return builder.build();
        });
    }

    public static Stream<CloudEvent> v03EventsWithStringExt() {
        return v03Events().map(ce -> {
            io.cloudevents.v03.CloudEventBuilder builder = CloudEventUtils.buildV03(ce);
            ce.getExtensions().forEach((k, v) -> builder.withExtension(k, v.toString()));
            return builder.build();
        });
    }

}
