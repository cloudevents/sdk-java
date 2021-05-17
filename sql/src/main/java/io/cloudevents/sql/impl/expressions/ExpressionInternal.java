package io.cloudevents.sql.impl.expressions;

import io.cloudevents.CloudEvent;
import io.cloudevents.sql.EvaluationRuntime;
import io.cloudevents.sql.impl.ExceptionThrower;
import org.antlr.v4.runtime.misc.Interval;

public interface ExpressionInternal {

    Interval expressionInterval();

    String expressionText();

    Object evaluate(EvaluationRuntime runtime, CloudEvent event, ExceptionThrower thrower);

    <T> T visit(ExpressionInternalVisitor<T> visitor);

}
