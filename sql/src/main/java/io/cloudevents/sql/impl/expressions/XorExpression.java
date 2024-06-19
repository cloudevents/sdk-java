package io.cloudevents.sql.impl.expressions;

import io.cloudevents.CloudEvent;
import io.cloudevents.sql.EvaluationRuntime;
import io.cloudevents.sql.ExceptionFactory;
import io.cloudevents.sql.Type;
import io.cloudevents.sql.impl.ExceptionFactoryImpl;
import io.cloudevents.sql.impl.ExpressionInternal;
import io.cloudevents.sql.impl.runtime.EvaluationResult;
import org.antlr.v4.runtime.misc.Interval;

public class XorExpression extends BaseBinaryExpression {

    public XorExpression(Interval expressionInterval, String expressionText, ExpressionInternal leftOperand, ExpressionInternal rightOperand) {
        super(expressionInterval, expressionText, leftOperand, rightOperand);
    }

    @Override
    public EvaluationResult evaluate(EvaluationRuntime runtime, CloudEvent event, ExceptionFactory exceptions) {
        EvaluationResult left = this.getLeftOperand().evaluate(runtime, event, exceptions);
        EvaluationResult right = this.getRightOperand().evaluate(runtime, event, exceptions);

        EvaluationResult x = castToBoolean(exceptions, left);
        EvaluationResult y = castToBoolean(exceptions, right);
        return new EvaluationResult(Boolean.logicalXor(
            (Boolean)x.value(),
            (Boolean)y.value()
        )).wrap(x).wrap(y);
    }
}
