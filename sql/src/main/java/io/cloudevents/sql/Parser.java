package io.cloudevents.sql;

import io.cloudevents.sql.impl.ParserImpl;

public interface Parser {

    Expression parse(String inputExpression);

    /**
     * Parse with default parser instance
     *
     * @param inputExpression input expression
     * @return the parsed expression
     */
    static Expression parseDefault(String inputExpression) {
        return ParserImpl.getInstance().parse(inputExpression);
    }

}
