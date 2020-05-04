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

package io.cloudevents;

public class CloudEventVisitException extends RuntimeException {

    public enum MessageVisitExceptionKind {
        INVALID_SPEC_VERSION,
        INVALID_ATTRIBUTE_NAME,
        INVALID_ATTRIBUTE_TYPE,
        INVALID_ATTRIBUTE_VALUE,
        INVALID_EXTENSION_TYPE,
        OTHER
    }

    private MessageVisitExceptionKind kind;

    public CloudEventVisitException(MessageVisitExceptionKind kind, Throwable cause) {
        super(cause);
        this.kind = kind;
    }

    public CloudEventVisitException(MessageVisitExceptionKind kind, String message) {
        super(message);
        this.kind = kind;
    }

    public CloudEventVisitException(MessageVisitExceptionKind kind, String message, Throwable cause) {
        super(message, cause);
        this.kind = kind;
    }

    public MessageVisitExceptionKind getKind() {
        return kind;
    }

    public static CloudEventVisitException newInvalidSpecVersion(String specVersion) {
        return new CloudEventVisitException(
            MessageVisitExceptionKind.INVALID_ATTRIBUTE_TYPE,
            "Invalid specversion: " + specVersion
        );
    }

    public static CloudEventVisitException newInvalidAttributeName(String attributeName) {
        return new CloudEventVisitException(
            MessageVisitExceptionKind.INVALID_ATTRIBUTE_NAME,
            "Invalid attribute: " + attributeName
        );
    }

    public static CloudEventVisitException newInvalidAttributeType(String attributeName, Class<?> clazz) {
        return new CloudEventVisitException(
            MessageVisitExceptionKind.INVALID_ATTRIBUTE_TYPE,
            "Invalid attribute type for \"" + attributeName + "\": " + clazz.getCanonicalName()
        );
    }

    public static CloudEventVisitException newInvalidAttributeValue(String attributeName, Object value, Throwable cause) {
        return new CloudEventVisitException(
            MessageVisitExceptionKind.INVALID_ATTRIBUTE_VALUE,
            "Invalid attribute value for \"" + attributeName + "\": " + value,
            cause
        );
    }

    public static CloudEventVisitException newInvalidExtensionType(String extensionName, Class<?> clazz) {
        return new CloudEventVisitException(
            MessageVisitExceptionKind.INVALID_EXTENSION_TYPE,
            "Invalid extension type for \"" + extensionName + "\": " + clazz.getCanonicalName()
        );
    }

    public static CloudEventVisitException newOther(Throwable cause) {
        return new CloudEventVisitException(
            MessageVisitExceptionKind.OTHER,
            cause
        );
    }
}
