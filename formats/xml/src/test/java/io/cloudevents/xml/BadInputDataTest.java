package io.cloudevents.xml;

import io.cloudevents.core.format.EventFormat;
import io.cloudevents.rw.CloudEventRWException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * A seperate Test set to hold the test cases related
 * to dealing with invalid representations
 */
public class BadInputDataTest {

    private final EventFormat format = new XMLFormat();

    @ParameterizedTest
    @MethodSource("badDataTestFiles")
    public void verifyRejection(File testFile) throws IOException {

        byte[] data = TestUtils.getData(testFile);

        assertThatExceptionOfType(CloudEventRWException.class).isThrownBy(() -> {format.deserialize(data);});
    }

    /**
     * Obtain a list of all the "bad exmaple" resource files
     * @return
     * @throws IOException
     */
    public static Stream<Arguments>  badDataTestFiles() throws IOException {

        File fileDir = TestUtils.getFile("bad");

        File[] fileList = fileDir.listFiles();
        List<Arguments> argList = new ArrayList<>();

        for (File f : fileList) {
            argList.add(Arguments.of(f));
        }

        return argList.stream();
    }
}
