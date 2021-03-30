package io.cloudevents.sql;

import io.cloudevents.core.test.Data;
import org.junit.jupiter.api.Test;

import static io.cloudevents.sql.asserts.MyAssertions.assertThat;

public class BinaryMathExpressionTest {

    @Test
    void operatorsPrecedence() {
        assertThat(Parser.parseDefault("4 * 2 + 4 / 2").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asInteger()
            .isEqualTo(10);

        assertThat(Parser.parseDefault("4 * (2 + 4) / 2").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asInteger()
            .isEqualTo(12);
    }

    @Test
    void truncatedDivision() {
        assertThat(Parser.parseDefault("5 / 3").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asInteger()
            .isEqualTo(1);
    }

    @Test
    void mod() {
        assertThat(Parser.parseDefault("5 % 2").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asInteger()
            .isEqualTo(1);
    }

    @Test
    void algebraicSum() {
        assertThat(Parser.parseDefault("4 + 1").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asInteger()
            .isEqualTo(5);
        assertThat(Parser.parseDefault("-4 + 1").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asInteger()
            .isEqualTo(-3);
        assertThat(Parser.parseDefault("-4 + -1").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asInteger()
            .isEqualTo(-5);
        assertThat(Parser.parseDefault("4 + -1").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asInteger()
            .isEqualTo(3);
        assertThat(Parser.parseDefault("4 - 1").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asInteger()
            .isEqualTo(3);
        assertThat(Parser.parseDefault("-4 - 1").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asInteger()
            .isEqualTo(-5);
    }

    @Test
    void implicitCasting() {
        assertThat(Parser.parseDefault("'5' + 3").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asInteger()
            .isEqualTo(8);
        assertThat(Parser.parseDefault("5 + '3'").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asInteger()
            .isEqualTo(8);
        assertThat(Parser.parseDefault("'5' + '3'").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asInteger()
            .isEqualTo(8);
        assertThat(Parser.parseDefault("5 + TRUE").evaluate(Data.V1_MIN))
            .hasFailure(EvaluationException.Kind.INVALID_CAST)
            .asInteger()
            .isEqualTo(5);

        assertThat(Parser.parseDefault("'5avc4' + 10").evaluate(Data.V1_MIN))
            .hasFailure(EvaluationException.Kind.INVALID_CAST)
            .asInteger()
            .isEqualTo(10);
    }

}
