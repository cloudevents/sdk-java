package io.cloudevents.sql.asserts;

import io.cloudevents.sql.Result;
import org.assertj.core.api.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.*;

public class ResultAssert extends AbstractAssert<ResultAssert, Result> {

    public ResultAssert(Result actual) {
        super(actual, ResultAssert.class);
    }

    public AbstractBooleanAssert<?> asBoolean() {
        isNotNull();
        return assertThat(this.actual.value())
            .asInstanceOf(BOOLEAN);
    }

    public AbstractIntegerAssert<?> asInteger() {
        isNotNull();
        return assertThat(this.actual.value())
            .asInstanceOf(INTEGER);
    }

    public AbstractStringAssert<?> asString() {
        isNotNull();
        return assertThat(this.actual.value())
            .asInstanceOf(STRING);
    }

    public ResultAssert isNotFailed() {
        isNotNull();
        assertThat(this.actual.isFailed()).isFalse();
        return this;
    }

    public ResultAssert isFailed() {
        isNotNull();
        assertThat(this.actual.isFailed()).isTrue();
        return this;
    }

    public ObjectAssert<Object> value() {
        isNotNull();
        return assertThat(this.actual);
    }

    public AbstractThrowableAssert<?, ? extends Throwable> cause() {
        return assertThat(this.actual.cause());
    }

}
