package io.cloudevents.sql.impl;

import io.cloudevents.CloudEvent;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.regex.Pattern;

public class ValueExpression implements ExpressionInternal {

    private final Object value;

    public ValueExpression(Object value) {
        this.value = value;
    }

    @Override
    public Object evaluate(EvaluationContext ctx, CloudEvent event) {
        return value;
    }

    public static ValueExpression fromIntegerLiteral(TerminalNode node) {
        return new ValueExpression(Integer.parseInt(node.getText()));
    }

    public static ValueExpression fromSQuotedStringLiteral(TerminalNode node) {
        String val = node.getText();
        val = val.substring(1, val.length() - 1);
        val = val.replaceAll(Pattern.quote("\\'"), "'");
        return new ValueExpression(val);
    }

    public static ValueExpression fromDQuotedStringLiteral(TerminalNode node) {
        String val = node.getText();
        val = val.substring(1, val.length() - 1);
        val = val.replaceAll(Pattern.quote("\\\""), "\"");
        return new ValueExpression(val);
    }

}
