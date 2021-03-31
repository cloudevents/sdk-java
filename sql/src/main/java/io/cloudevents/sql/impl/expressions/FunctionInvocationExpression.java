package io.cloudevents.sql.impl.expressions;

import io.cloudevents.CloudEvent;
import io.cloudevents.sql.EvaluationException;
import io.cloudevents.sql.EvaluationRuntime;
import io.cloudevents.sql.Function;
import io.cloudevents.sql.impl.EvaluationContextImpl;
import io.cloudevents.sql.impl.EvaluationExceptions;
import io.cloudevents.sql.impl.ExpressionInternal;
import org.antlr.v4.runtime.misc.Interval;

import java.util.ArrayList;
import java.util.List;

public class FunctionInvocationExpression extends BaseExpression {

    private final String functionName;
    private final List<ExpressionInternal> arguments;

    public FunctionInvocationExpression(Interval expressionInterval, String expressionText, String functionName, List<ExpressionInternal> arguments) {
        super(expressionInterval, expressionText);
        this.functionName = functionName.toUpperCase();
        this.arguments = arguments;
    }

    @Override
    public Object evaluate(EvaluationRuntime runtime, CloudEvent event, EvaluationExceptions exceptions) {
        Function function;
        try {
            function = runtime.resolveFunction(functionName, arguments.size());
        } catch (Exception e) {
            exceptions.appendException(
                EvaluationException.cannotDispatchFunction(expressionInterval(), expressionText(), functionName, e)
            );
            return "";
        }

        List<Object> computedArguments = new ArrayList<>(arguments.size());
        for (int i = 0; i < arguments.size(); i++) {
            ExpressionInternal expr = arguments.get(i);
            Object computed = expr.evaluate(runtime, event, exceptions);
            Object casted = runtime
                .cast(new EvaluationContextImpl(expressionInterval(), expressionText(), exceptions), computed, function.typeOfParameter(i));
            computedArguments.add(casted);
        }

        return function.invoke(
            new EvaluationContextImpl(expressionInterval(), expressionText(), exceptions),
            runtime,
            event,
            computedArguments
        );
    }
}
