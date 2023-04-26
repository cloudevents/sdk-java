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

package io.cloudevents.rocketmq.impl;

import io.cloudevents.core.message.impl.MessageUtils;
import java.util.Map;

/**
 * Constants and methods used throughout the RocketMQ binding for cloud events.
 */
public final class RocketmqConstants {
    private RocketmqConstants() {
        // prevent instantiation
    }

    public static final byte[] EMPTY_BODY = new byte[] {(byte) '\0'};

    /**
     * The prefix name for CloudEvent attributes for use in properties of a RocketMQ message.
     */
    public static final String CE_PREFIX = "CE_";

    public static final Map<String, String> ATTRIBUTES_TO_PROPERTY_NAMES = MessageUtils.generateAttributesToHeadersMapping(CEA -> CE_PREFIX + CEA);

    public static final String PROPERTY_CONTENT_TYPE = "CE_contenttype";
    public static final String MESSAGE_PROPERTY_SPEC_VERSION = ATTRIBUTES_TO_PROPERTY_NAMES.get("specversion");
}
