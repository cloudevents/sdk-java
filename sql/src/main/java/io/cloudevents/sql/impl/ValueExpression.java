package io.cloudevents.sql.impl;

import io.cloudevents.CloudEvent;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.regex.Pattern;

public class ValueExpression extends BaseExpression {

    private final Object value;

    public ValueExpression(Interval expressionInterval, String expressionText, Object value) {
        super(expressionInterval, expressionText);
        this.value = value;
    }

    @Override
    public Object evaluate(EvaluationContextImpl ctx, CloudEvent event) {
        return value;
    }

    public static ValueExpression fromIntegerLiteral(TerminalNode node) {
        return new ValueExpression(node.getSourceInterval(), node.getText(), Integer.parseInt(node.getText()));
    }

    public static ValueExpression fromSQuotedStringLiteral(TerminalNode node) {
        String val = node.getText();
        val = val.substring(1, val.length() - 1);
        val = val.replaceAll(Pattern.quote("\\'"), "'");
        return new ValueExpression(node.getSourceInterval(), node.getText(), val);
    }

    public static ValueExpression fromDQuotedStringLiteral(TerminalNode node) {
        String val = node.getText();
        val = val.substring(1, val.length() - 1);
        val = val.replaceAll(Pattern.quote("\\\""), "\"");
        return new ValueExpression(node.getSourceInterval(), node.getText(), val);
    }

}
