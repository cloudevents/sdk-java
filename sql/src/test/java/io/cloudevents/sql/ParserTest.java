package io.cloudevents.sql;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class ParserTest {

    @Test
    void parseException() {
        assertThatThrownBy(() -> Parser.parseDefault("ABC("))
            .isInstanceOf(ParseException.class)
            .extracting("kind")
            .isEqualTo(ParseException.ErrorKind.RECOGNITION_ERROR);
    }

}
