package io.cloudevents.sql.impl.expressions;

import io.cloudevents.CloudEvent;
import io.cloudevents.sql.*;
import io.cloudevents.sql.impl.ExpressionInternal;
import io.cloudevents.sql.impl.ExpressionInternalVisitor;
import io.cloudevents.sql.impl.runtime.EvaluationContextImpl;
import io.cloudevents.sql.impl.runtime.EvaluationResult;
import io.cloudevents.sql.impl.runtime.TypeCastingProvider;
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
    public EvaluationResult evaluate(EvaluationRuntime runtime, CloudEvent event, ExceptionFactory exceptionFactory) {
        EvaluationContext context = new EvaluationContextImpl(expressionInterval(), expressionText(), exceptionFactory);

        Function function;
        try {
            function = runtime.resolveFunction(functionName, arguments.size());
        } catch (Exception e) {
            return new EvaluationResult(false, exceptionFactory.cannotDispatchFunction(expressionInterval(), expressionText(), functionName, e));
        }

        List<Object> computedArguments = new ArrayList<>(arguments.size());
        List<EvaluationException> exceptions =  new ArrayList<>(); // used to accumulate any exceptions encountered while evaluating the arguments to the function
        for (int i = 0; i < arguments.size(); i++) {
            ExpressionInternal expr = arguments.get(i);
            EvaluationResult computed = expr.evaluate(runtime, event, exceptionFactory);
            EvaluationResult casted = TypeCastingProvider
                .cast(context, computed, function.typeOfParameter(i));
            if (casted.causes() != null) {
                exceptions.addAll(casted.causes());
            }
            computedArguments.add(casted.value());
        }

        return function.invoke(
            context,
            runtime,
            event,
            computedArguments
        ).wrap(exceptions);
    }

    @Override
    public <T> T visit(ExpressionInternalVisitor<T> visitor) {
        return visitor.visitFunctionInvocationExpression(this);
    }
}
