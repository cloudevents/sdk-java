package io.cloudevents.sql;

import io.cloudevents.core.test.Data;
import org.junit.jupiter.api.Test;

import static io.cloudevents.sql.asserts.MyAssertions.assertThat;

public class CastingFunctionsTest {

    @Test
    void intFunction() {
        assertThat(Parser.parseDefault("INT('1')").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asInteger()
            .isEqualTo(1);
        assertThat(Parser.parseDefault("INT('-1')").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asInteger()
            .isEqualTo(-1);
        assertThat(Parser.parseDefault("INT(TRUE)").evaluate(Data.V1_MIN))
            .hasFailure(EvaluationException.Kind.INVALID_CAST);
    }

}
