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
import com.google.protobuf.Message;

import java.util.Objects;

class ProtoDataWrapper implements ProtoCloudEventData {

    private final Any protoAny;

    ProtoDataWrapper(Message protoMessage) {

        Objects.requireNonNull(protoMessage);

        if (protoMessage instanceof Any any) {
            protoAny = any;
        } else {
            protoAny = Any.pack(protoMessage);
        }
    }

    @Override
    public Any getAny() {
        return protoAny;
    }

    @Override
    public byte[] toBytes() {
        return protoAny.toByteArray();
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return (true);
        }

        if (!(obj instanceof ProtoDataWrapper)) {
            return (false);
        }

        // Now compare the actual data
        ProtoDataWrapper rhs = (ProtoDataWrapper) obj;

        final Any lhsAny = getAny();
        final Any rhsAny = rhs.getAny();

        // This is split out for readability.
        //  1. Sanity compare the 'Any' references.
        //  2. Compare the content in terms onf an 'Any'.
        //     - Verify the types match
        //     - Verify the values match.

        // NULL checks not required as object cannot be built
        // with a null.

        if (lhsAny == rhsAny) {
            return true;
        }

        final boolean typesMatch = (ProtoSupport.extractMessageType(lhsAny).equals(ProtoSupport.extractMessageType(rhsAny)));

        if (typesMatch) {
            return lhsAny.getValue().equals(rhsAny.getValue());
        } else {
            return false;
        }

    }

}
