package io.cloudevents.sql.asserts;

import io.cloudevents.sql.EvaluationException;
import io.cloudevents.sql.Result;
import org.assertj.core.api.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.*;

public class ResultAssert extends AbstractAssert<ResultAssert, Result> {

    public ResultAssert(Result actual) {
        super(actual, ResultAssert.class);
    }

    public BooleanAssert asBoolean() {
        isNotNull();
        return (BooleanAssert) assertThat(this.actual.value())
            .asInstanceOf(BOOLEAN);
    }

    public IntegerAssert asInteger() {
        isNotNull();
        return (IntegerAssert) assertThat(this.actual.value())
            .asInstanceOf(INTEGER);
    }

    public StringAssert asString() {
        isNotNull();
        return (StringAssert) assertThat(this.actual.value())
            .asInstanceOf(STRING);
    }

    public ResultAssert isNotFailed() {
        isNotNull();
        assertThat(this.actual.isFailed())
            .withFailMessage(
                "Failed with causes: %s",
                this.actual.causes()
            )
            .isFalse();
        return this;
    }

    public ResultAssert isFailed() {
        isNotNull();
        assertThat(this.actual.isFailed()).isTrue();
        return this;
    }

    public ObjectAssert<Object> value() {
        isNotNull();
        return assertThat(this.actual.value());
    }

    public AbstractCollectionAssert<?, ?, EvaluationException, ObjectAssert<EvaluationException>> causes() {
        return assertThat(this.actual.causes());
    }

    public ResultAssert hasFailure(EvaluationException.ErrorKind exceptionErrorKind) {
        causes()
            .anySatisfy(ex ->
                assertThat(ex.getKind())
                    .isEqualTo(exceptionErrorKind)
            );

        return this;
    }

    public ResultAssert hasFailure(EvaluationException.ErrorKind exceptionErrorKind, String expression) {
        causes()
            .anySatisfy(ex -> {
                assertThat(ex.getKind())
                    .isEqualTo(exceptionErrorKind);
                assertThat(ex.getExpressionText())
                    .isEqualTo(expression);
            });

        return this;
    }
}
