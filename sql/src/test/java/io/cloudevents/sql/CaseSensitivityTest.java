package io.cloudevents.sql;

import io.cloudevents.core.test.Data;
import io.cloudevents.sql.impl.EvaluationResult;
import org.junit.jupiter.api.Test;

import static io.cloudevents.sql.asserts.MyAssertions.assertThat;

public class CaseSensitivityTest {

    @Test
    void trueBooleanLiterals() {
        EvaluationResult expected = new EvaluationResult(true);

        assertThat(
            Parser.parseDefault("TRUE").evaluate(Data.V1_MIN)
        ).isEqualTo(expected);
        assertThat(
            Parser.parseDefault("true").evaluate(Data.V1_MIN)
        ).isEqualTo(expected);
        assertThat(
            Parser.parseDefault("tRuE").evaluate(Data.V1_MIN)
        ).isEqualTo(expected);
    }

    @Test
    void falseBooleanLiterals() {
        EvaluationResult expected = new EvaluationResult(false);

        assertThat(
            Parser.parseDefault("FALSE").evaluate(Data.V1_MIN)
        ).isEqualTo(expected);
        assertThat(
            Parser.parseDefault("false").evaluate(Data.V1_MIN)
        ).isEqualTo(expected);
        assertThat(
            Parser.parseDefault("FaLsE").evaluate(Data.V1_MIN)
        ).isEqualTo(expected);
    }
}
