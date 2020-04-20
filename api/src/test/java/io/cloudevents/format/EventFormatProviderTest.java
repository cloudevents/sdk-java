package io.cloudevents.format;

import io.cloudevents.mock.CSVFormat;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class EventFormatProviderTest {

    @Test
    void resolveCSV() {
        assertThat(EventFormatProvider.getInstance().resolveFormat("text/csv"))
            .isInstanceOf(CSVFormat.class);
    }

}
