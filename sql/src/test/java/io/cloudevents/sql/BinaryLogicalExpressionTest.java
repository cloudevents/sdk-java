package io.cloudevents.sql;

import io.cloudevents.core.test.Data;
import org.junit.jupiter.api.Test;

import static io.cloudevents.sql.asserts.MyAssertions.assertThat;

public class BinaryLogicalExpressionTest {

    @Test
    void and() {
        assertThat(Parser.parseDefault("FALSE AND FALSE").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asBoolean()
            .isFalse();

        assertThat(Parser.parseDefault("FALSE AND TRUE").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asBoolean()
            .isFalse();

        assertThat(Parser.parseDefault("TRUE AND FALSE").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asBoolean()
            .isFalse();

        assertThat(Parser.parseDefault("TRUE AND TRUE").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asBoolean()
            .isTrue();
    }

    @Test
    void or() {
        assertThat(Parser.parseDefault("FALSE OR FALSE").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asBoolean()
            .isFalse();

        assertThat(Parser.parseDefault("FALSE OR TRUE").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asBoolean()
            .isTrue();

        assertThat(Parser.parseDefault("TRUE OR FALSE").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asBoolean()
            .isTrue();

        assertThat(Parser.parseDefault("TRUE OR TRUE").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asBoolean()
            .isTrue();
    }

    @Test
    void xor() {
        assertThat(Parser.parseDefault("FALSE XOR FALSE").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asBoolean()
            .isFalse();

        assertThat(Parser.parseDefault("FALSE XOR TRUE").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asBoolean()
            .isTrue();

        assertThat(Parser.parseDefault("TRUE XOR FALSE").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asBoolean()
            .isTrue();

        assertThat(Parser.parseDefault("TRUE XOR TRUE").evaluate(Data.V1_MIN))
            .isNotFailed()
            .asBoolean()
            .isFalse();
    }

}
