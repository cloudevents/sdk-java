package io.cloudevents.sql.impl.expressions;

import io.cloudevents.CloudEvent;
import io.cloudevents.sql.EvaluationRuntime;
import io.cloudevents.sql.ExceptionFactory;
import io.cloudevents.sql.impl.ExceptionFactoryImpl;
import io.cloudevents.sql.impl.ExpressionInternalVisitor;
import io.cloudevents.sql.impl.parser.LiteralUtils;
import io.cloudevents.sql.impl.runtime.EvaluationResult;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.tree.TerminalNode;

public class ValueExpression extends BaseExpression {

    private final Object value;

    public ValueExpression(Interval expressionInterval, String expressionText, Object value) {
        super(expressionInterval, expressionText);
        this.value = value;
    }

    @Override
    public EvaluationResult evaluate(EvaluationRuntime runtime, CloudEvent event, ExceptionFactory thrower) {
        return new EvaluationResult(value);
    }

    @Override
    public <T> T visit(ExpressionInternalVisitor<T> visitor) {
        return visitor.visitValueExpression(this);
    }

    public Object getValue() {
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
