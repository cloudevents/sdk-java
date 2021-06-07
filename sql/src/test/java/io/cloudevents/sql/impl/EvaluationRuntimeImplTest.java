package io.cloudevents.sql.impl;

import io.cloudevents.sql.EvaluationException;
import io.cloudevents.sql.EvaluationRuntime;
import io.cloudevents.sql.Type;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

public class EvaluationRuntimeImplTest {

    @Test
    void castingFails() {
        assertThatCode(() -> EvaluationRuntime.getDefault().cast("123", Type.BOOLEAN))
            .isInstanceOf(EvaluationException.class);
    }

    @Test
    void castingSucceeds() {
        assertThat(EvaluationRuntime.getDefault().cast("TRUE", Type.BOOLEAN))
            .isEqualTo(true);
    }

}
