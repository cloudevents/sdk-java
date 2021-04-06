package io.cloudevents.sql.asserts;

import io.cloudevents.core.test.Data;
import io.cloudevents.sql.Parser;
import org.junit.jupiter.api.Test;

import static io.cloudevents.sql.asserts.MyAssertions.assertThat;

public class IntegerBuiltinFunctionsTest {

    @Test
    void absFunction() {
        assertThat(Parser.parseDefault("ABS(10)").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asInteger()
            .isEqualTo(10);
        assertThat(Parser.parseDefault("ABS(-10)").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asInteger()
            .isEqualTo(10);
        assertThat(Parser.parseDefault("ABS(0)").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asInteger()
            .isEqualTo(0);
    }

}
