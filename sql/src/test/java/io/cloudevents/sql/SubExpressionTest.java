package io.cloudevents.sql;

import io.cloudevents.core.test.Data;
import org.junit.jupiter.api.Test;

import static io.cloudevents.sql.asserts.MyAssertions.assertThat;

public class SubExpressionTest {

    @Test
    void subExpressionLiteral() {
        assertThat(Parser.parseDefault("(TRUE)").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asBoolean()
            .isTrue();
    }

    @Test
    void someMath() {
        assertThat(Parser.parseDefault("4 * (2 + 3)").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asInteger()
            .isEqualTo(20);
        assertThat(Parser.parseDefault("(2 + 3) * 4").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asInteger()
            .isEqualTo(20);
    }

}
