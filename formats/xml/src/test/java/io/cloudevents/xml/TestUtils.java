package io.cloudevents.xml;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;

public class TestUtils {

    /**
     * Get a File forn item in the resource path.
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
