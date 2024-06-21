package io.cloudevents.sql.impl.expressions;

import io.cloudevents.CloudEvent;
import io.cloudevents.sql.EvaluationRuntime;
import io.cloudevents.sql.ExceptionFactory;
import io.cloudevents.sql.Type;
import io.cloudevents.sql.impl.ExpressionInternal;
import io.cloudevents.sql.impl.runtime.EvaluationResult;
import org.antlr.v4.runtime.misc.Interval;

import java.util.function.BiFunction;

public class IntegerComparisonBinaryExpression extends BaseBinaryExpression {

    public enum Operation {
        LESS((x, y) -> x < y),
        LESS_OR_EQUAL((x, y) -> x <= y),
        GREATER((x, y) -> x > y),
        GREATER_OR_EQUAL((x, y) -> x >= y);

        private final BiFunction<Integer, Integer, Boolean> fn;

        Operation(BiFunction<Integer, Integer, Boolean> fn) {
            this.fn = fn;
        }

        boolean evaluate(int a, int b) {
            return this.fn.apply(a, b);
        }
    }

    private final Operation operation;

    public IntegerComparisonBinaryExpression(Interval expressionInterval, String expressionText, ExpressionInternal leftOperand, ExpressionInternal rightOperand, Operation operation) {
        super(expressionInterval, expressionText, leftOperand, rightOperand);
        this.operation = operation;
    }

    @Override
    public EvaluationResult evaluate(EvaluationRuntime runtime, CloudEvent event, ExceptionFactory exceptionFactory) {
        EvaluationResult left = this.getLeftOperand().evaluate(runtime, event, exceptionFactory);
        EvaluationResult right = this.getRightOperand().evaluate(runtime, event, exceptionFactory);

        if (left.isMissingAttributeException() || right.isMissingAttributeException()) {
            return left.wrapExceptions(right).copyWithDefaultValueForType(Type.BOOLEAN);
        }

        EvaluationResult x = castToInteger(exceptionFactory, left);
        EvaluationResult y = castToInteger(exceptionFactory, right);

        return new EvaluationResult(this.operation.evaluate(
            (Integer)x.value(),
            (Integer)y.value()
        )).wrapExceptions(x).wrapExceptions(y);
    }

}
