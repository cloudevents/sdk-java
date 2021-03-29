package io.cloudevents.sql.impl;

import io.cloudevents.sql.EvaluationContext;
import io.cloudevents.sql.EvaluationException;
import io.cloudevents.sql.Runtime;
import org.antlr.v4.runtime.misc.Interval;

import java.util.ArrayList;
import java.util.List;

public class EvaluationContextImpl implements EvaluationContext {

    private final Runtime runtime;

    private Interval expressionInterval;
    private String expressionText;
    private List<EvaluationException> evaluationExceptions;

    public EvaluationContextImpl(Runtime runtime) {
        this.runtime = runtime;
    }

    @Override
    public Runtime getRuntime() {
        return this.runtime;
    }

    @Override
    public Interval expressionInterval() {
        return this.expressionInterval;
    }

    @Override
    public String expressionText() {
        return this.expressionText;
    }

    @Override
    public void appendException(EvaluationException exception) {
        if (this.evaluationExceptions == null) {
            this.evaluationExceptions = new ArrayList<>();
        }
        this.evaluationExceptions.add(exception);
    }

    public EvaluationContextImpl setExpressionInterval(Interval expressionInterval) {
        this.expressionInterval = expressionInterval;
        return this;
    }

    public EvaluationContextImpl setExpressionText(String expressionText) {
        this.expressionText = expressionText;
        return this;
    }

    public List<EvaluationException> getEvaluationExceptions() {
        return evaluationExceptions;
    }
}
