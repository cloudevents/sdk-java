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

package io.cloudevents.protobuf;

import com.google.protobuf.Any;

/**
 * General support functions.
 */

final class ProtoSupport {

    // Prevent Instantiation
    private ProtoSupport() {
    }

    /**
     * Determine if the given content type indicates that
     * content is textual.
     */
    static boolean isTextContent(String contentType) {

        if (contentType == null) {
            return false;
        }

        return contentType.startsWith("text/")
            || "application/json".equals(contentType)
            || "application/xml".equals(contentType)
            || contentType.endsWith("+json")
            || contentType.endsWith("+xml")
            ;
    }

    /**
     * Extract the Protobuf message type from an 'Any'
     * @param anyMessage
     * @return
     */
    static String extractMessageType(final Any anyMessage) {
        final String typeUrl = anyMessage.getTypeUrl();
        final String[] parts = typeUrl.split("/");

        return parts[parts.length -1];
    }
}
