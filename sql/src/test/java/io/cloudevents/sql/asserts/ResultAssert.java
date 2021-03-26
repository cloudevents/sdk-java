package io.cloudevents.sql.asserts;

import io.cloudevents.sql.Result;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.AbstractThrowableAssert;
import org.assertj.core.api.ObjectAssert;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.BOOLEAN;

public class ResultAssert extends AbstractAssert<ResultAssert, Result> {

    public ResultAssert(Result actual) {
        super(actual, ResultAssert.class);
    }

    public ResultAssert isTrue() {
        isNotNull();
        assertThat(this.actual.value())
            .asInstanceOf(BOOLEAN)
            .isTrue();
        return this;
    }

    public ResultAssert isFalse() {
        isNotNull();
        assertThat(this.actual.value())
            .asInstanceOf(BOOLEAN)
            .isFalse();
        return this;
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
