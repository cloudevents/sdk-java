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
        assertThat(Parser.parseDefault("INT(1)").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asInteger()
            .isEqualTo(1);
        assertThat(Parser.parseDefault("INT(-1)").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asInteger()
            .isEqualTo(-1);
        assertThat(Parser.parseDefault("INT(TRUE)").evaluate(Data.V1_MIN))
            .hasFailure(EvaluationException.ErrorKind.INVALID_CAST);
        assertThat(Parser.parseDefault("INT('ABC')").evaluate(Data.V1_MIN))
            .hasFailure(EvaluationException.ErrorKind.INVALID_CAST);
    }

    @Test
    void boolFunction() {
        assertThat(Parser.parseDefault("BOOL('TRUE')").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asBoolean()
            .isEqualTo(true);
        assertThat(Parser.parseDefault("BOOL('false')").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asBoolean()
            .isEqualTo(false);
        assertThat(Parser.parseDefault("BOOL(TRUE)").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asBoolean()
            .isEqualTo(true);
        assertThat(Parser.parseDefault("BOOL(FALSE)").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asBoolean()
            .isEqualTo(false);
        assertThat(Parser.parseDefault("BOOL('ABC')").evaluate(Data.V1_MIN))
            .hasFailure(EvaluationException.ErrorKind.INVALID_CAST);
        assertThat(Parser.parseDefault("BOOL(1)").evaluate(Data.V1_MIN))
            .hasFailure(EvaluationException.ErrorKind.INVALID_CAST);
    }

    @Test
    void stringFunction() {
        assertThat(Parser.parseDefault("STRING(TRUE)").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asString()
            .isEqualTo("true");
        assertThat(Parser.parseDefault("STRING(FALSE)").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asString()
            .isEqualTo("false");
        assertThat(Parser.parseDefault("STRING(1)").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asString()
            .isEqualTo("1");
        assertThat(Parser.parseDefault("STRING(-1)").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asString()
            .isEqualTo("-1");
        assertThat(Parser.parseDefault("STRING(\"ABC\")").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asString()
            .isEqualTo("ABC");
    }

    @Test
    void isBoolFunction() {
        assertThat(Parser.parseDefault("IS_BOOL('true')").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asBoolean()
            .isEqualTo(true);
        assertThat(Parser.parseDefault("IS_BOOL('FALSE')").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asBoolean()
            .isEqualTo(true);
        assertThat(Parser.parseDefault("IS_BOOL(1)").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asBoolean()
            .isEqualTo(false);
        assertThat(Parser.parseDefault("IS_BOOL('abc')").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asBoolean()
            .isEqualTo(false);
    }

    @Test
    void isIntFunction() {
        assertThat(Parser.parseDefault("IS_INT('-1')").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asBoolean()
            .isEqualTo(true);
        assertThat(Parser.parseDefault("IS_INT('1')").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asBoolean()
            .isEqualTo(true);
        assertThat(Parser.parseDefault("IS_INT('abc')").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asBoolean()
            .isEqualTo(false);
        assertThat(Parser.parseDefault("IS_INT(true)").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asBoolean()
            .isEqualTo(false);
    }

}
