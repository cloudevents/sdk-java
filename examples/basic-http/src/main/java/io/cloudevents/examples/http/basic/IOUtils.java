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
 */

 package io.cloudevents.examples.http.basic;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

// based on apache's commons-io
// which is licensed under the Apache License, Version 2.0
// https://commons.apache.org/proper/commons-io/
// https://github.com/apache/commons-io/blob/master/src/main/java/org/apache/commons/io/IOUtils.java

public final class IOUtils {

    private IOUtils() {}

    // since Java 9+ you may call InputStream.readAllBytes() instead of this method
    public static byte[] toByteArray(InputStream body) throws IOException {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            byte[] buff = new byte[(1<<10) * 8];
            int read;
            while ((read = body.read(buff)) != -1) {
                byteArrayOutputStream.write(buff, 0, read);
            }
            return byteArrayOutputStream.toByteArray();
        }
    }
}
