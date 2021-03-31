package io.cloudevents.sql;

import io.cloudevents.core.test.Data;
import org.junit.jupiter.api.Test;

import static io.cloudevents.sql.asserts.MyAssertions.assertThat;

public class NegateExpressionTest {

    @Test
    void intInput() {
        assertThat(Parser.parseDefault("-10").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asInteger()
            .isEqualTo(-10);

        assertThat(Parser.parseDefault("--10").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asInteger()
            .isEqualTo(10);
    }

    @Test
    void stringInput() {
        assertThat(Parser.parseDefault("-'10'").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asInteger()
            .isEqualTo(-10);

        assertThat(Parser.parseDefault("--'10'").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asInteger()
            .isEqualTo(10);
    }

    @Test
    void invalidCast() {
        assertThat(Parser.parseDefault("-TRUE").evaluate(Data.V1_MIN))
            .hasFailure(EvaluationException.ErrorKind.INVALID_CAST)
            .asInteger()
            .isEqualTo(0);
    }

}
