package io.cloudevents.sql.impl.expressions;

import io.cloudevents.sql.EvaluationRuntime;
import io.cloudevents.sql.impl.EvaluationExceptions;
import io.cloudevents.sql.impl.ExpressionInternal;
import org.antlr.v4.runtime.misc.Interval;

import java.util.function.BiFunction;

public class MathBinaryExpression extends BaseBinaryExpression {

    public enum Operation {
        SUM(Integer::sum),
        DIFFERENCE((x, y) -> x - y),
        MULTIPLICATION((x, y) -> x * y),
        DIVISION((x, y) -> x / y),
        MODULE((x, y) -> x % y);

        private final BiFunction<Integer, Integer, Integer> fn;

        Operation(BiFunction<Integer, Integer, Integer> fn) {
            this.fn = fn;
        }

        int evaluate(int a, int b) {
            return this.fn.apply(a, b);
        }
    }

    private final Operation operation;

    public MathBinaryExpression(Interval expressionInterval, String expressionText, ExpressionInternal leftOperand, ExpressionInternal rightOperand, Operation operation) {
        super(expressionInterval, expressionText, leftOperand, rightOperand);
        this.operation = operation;
    }

    @Override
    Object evaluate(EvaluationRuntime runtime, Object left, Object right, EvaluationExceptions exceptions) {
        return this.operation.evaluate(
            castToInteger(runtime, exceptions, left),
            castToInteger(runtime, exceptions, right)
        );
    }

}
