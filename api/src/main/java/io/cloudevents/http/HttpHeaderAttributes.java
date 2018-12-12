package io.cloudevents.http;

import io.cloudevents.SpecVersion;
import io.cloudevents.attributes.HeaderAttributes;

public interface HttpHeaderAttributes extends HeaderAttributes {

    static HeaderAttributes getHttpAttributesForSpec(final SpecVersion specVersion) {

        switch (specVersion) {

            case V_01: return new V01HttpTransportMappers();
            case V_02:
            case DEFAULT: return new V02HttpTransportMappers();
        }

        // you should not be here!
        throw new IllegalArgumentException("Could not find proper version");
    }
}
