package io.cloudevents.sql.impl;

import org.antlr.v4.runtime.misc.Interval;

import java.util.function.BiPredicate;

public class LogicalBinaryExpression extends BaseBinaryExpression {

    enum Operation {
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

    protected LogicalBinaryExpression(Interval expressionInterval, String expressionText, ExpressionInternal leftOperand, ExpressionInternal rightOperand, Operation operation) {
        super(expressionInterval, expressionText, leftOperand, rightOperand);
        this.operation = operation;
    }

    @Override
    Object evaluate(EvaluationContextImpl ctx, Object left, Object right) {
        return this.operation.evaluate(
            castToBoolean(ctx, left),
            castToBoolean(ctx, right)
        );
    }
}
