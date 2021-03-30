package io.cloudevents.sql.impl;

import io.cloudevents.CloudEvent;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.tree.TerminalNode;

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
        return new ValueExpression(node.getSourceInterval(), node.getText(), LiteralUtils.parseSQuotedStringLiteral(node));
    }

    public static ValueExpression fromDQuotedStringLiteral(TerminalNode node) {
        return new ValueExpression(node.getSourceInterval(), node.getText(), LiteralUtils.parseDQuotedStringLiteral(node));
    }

}
