package io.cloudevents.sql.impl;

import io.cloudevents.sql.Expression;
import io.cloudevents.sql.Parser;
import io.cloudevents.sql.generated.CESQLParserLexer;
import io.cloudevents.sql.generated.CESQLParserParser;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

public class ParserImpl implements Parser {

    private static class SingletonContainer {
        private final static ParserImpl INSTANCE = new ParserImpl();
    }

    /**
     * @return instance of {@link ParserImpl}
     */
    public static Parser getInstance() {
        return ParserImpl.SingletonContainer.INSTANCE;
    }

    public ParserImpl() {
    }

    @Override
    public Expression parse(String inputExpression) {
        CharStream s = CharStreams.fromString(inputExpression);
        CaseChangingCharStream upperInput = new CaseChangingCharStream(s, true);
        CESQLParserLexer lexer = new CESQLParserLexer(upperInput);

        CommonTokenStream tokens = new CommonTokenStream(lexer);

        CESQLParserParser parser = new CESQLParserParser(tokens);

        // Start parsing from cesql rule
        ParseTree tree = parser.cesql();

        ExpressionInternal internal = new ExpressionTranslatorVisitor().visit(tree);

        return new ExpressionImpl(internal);
    }

}
