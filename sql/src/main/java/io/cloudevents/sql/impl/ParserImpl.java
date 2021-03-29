package io.cloudevents.sql.impl;

import io.cloudevents.sql.Expression;
import io.cloudevents.sql.ParseException;
import io.cloudevents.sql.Parser;
import io.cloudevents.sql.generated.CESQLParserLexer;
import io.cloudevents.sql.generated.CESQLParserParser;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.ArrayList;
import java.util.List;

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

        List<ParseException> parseExceptionList = new ArrayList<>();
        parser.removeErrorListeners();
        parser.addErrorListener(new ConsoleErrorListener() {
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
                super.syntaxError(recognizer, offendingSymbol, line, charPositionInLine, msg, e);
                parseExceptionList.add(
                    ParseException.recognitionError(e, msg)
                );
            }
        });

        // Start parsing from cesql rule
        ParseTree tree = parser.cesql();

        if (!parseExceptionList.isEmpty()) {
            throw parseExceptionList.get(0);
        }

        ExpressionInternal internal = new ExpressionTranslatorVisitor().visit(tree);

        return new ExpressionImpl(internal);
    }

}
