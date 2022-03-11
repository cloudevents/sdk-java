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
package io.cloudevents.xml;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;

class TestUtils {

    /**
     * Get a File forn item in the resource path.
     *
     * @param filename
     * @return
     * @throws IOException
     */
    static File getFile(String filename) throws IOException {
        URL file = Thread.currentThread().getContextClassLoader().getResource(filename);
        assertThat(file).isNotNull();
        File dataFile = new File(file.getFile());
        assertThat(dataFile).isNotNull();
        return dataFile;
    }

    static Reader getReader(String filename) throws IOException {
        File dataFile = getFile(filename);
        return new FileReader(dataFile);
    }

    static byte[] getData(File dataFile) throws IOException {
        return Files.readAllBytes(dataFile.toPath());
    }

    static byte[] getData(String filename) throws IOException {
        File f = getFile(filename);
        return getData(f);
    }
}
