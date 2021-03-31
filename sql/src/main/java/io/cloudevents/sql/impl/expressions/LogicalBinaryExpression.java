package io.cloudevents.sql.impl.expressions;

import io.cloudevents.sql.EvaluationRuntime;
import io.cloudevents.sql.impl.EvaluationExceptions;
import io.cloudevents.sql.impl.ExpressionInternal;
import org.antlr.v4.runtime.misc.Interval;

import java.util.function.BiPredicate;

public class LogicalBinaryExpression extends BaseBinaryExpression {

    public enum Operation {
        AND(Boolean::logicalAnd),
        OR(Boolean::logicalOr),
        XOR(Boolean::logicalXor);

        private final BiPredicate<Boolean, Boolean> fn;

        Operation(BiPredicate<Boolean, Boolean> fn) {
            this.fn = fn;
        }

        boolean evaluate(boolean a, boolean b) {
            return this.fn.test(a, b);
        }
    }

    private final Operation operation;

    public LogicalBinaryExpression(Interval expressionInterval, String expressionText, ExpressionInternal leftOperand, ExpressionInternal rightOperand, Operation operation) {
        super(expressionInterval, expressionText, leftOperand, rightOperand);
        this.operation = operation;
    }

    @Override
    Object evaluate(EvaluationRuntime runtime, Object left, Object right, EvaluationExceptions exceptions) {
        return this.operation.evaluate(
            castToBoolean(runtime, exceptions, left),
            castToBoolean(runtime, exceptions, right)
        );
    }
}
