package io.cloudevents.sql.impl;

import io.cloudevents.sql.Expression;
import io.cloudevents.sql.ParseException;
import io.cloudevents.sql.Parser;
import io.cloudevents.sql.generated.CESQLParserLexer;
import io.cloudevents.sql.generated.CESQLParserParser;
import io.cloudevents.sql.impl.expressions.ExpressionInternal;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.ArrayList;
import java.util.BitSet;
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
        parser.addErrorListener(new ANTLRErrorListener() {
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
                parseExceptionList.add(
                    ParseException.recognitionError(e, msg)
                );
            }

            @Override
            public void reportAmbiguity(org.antlr.v4.runtime.Parser recognizer, DFA dfa, int startIndex, int stopIndex, boolean exact, BitSet ambigAlts, ATNConfigSet configs) {
            }

            @Override
            public void reportAttemptingFullContext(org.antlr.v4.runtime.Parser recognizer, DFA dfa, int startIndex, int stopIndex, BitSet conflictingAlts, ATNConfigSet configs) {
            }

            @Override
            public void reportContextSensitivity(org.antlr.v4.runtime.Parser recognizer, DFA dfa, int startIndex, int stopIndex, int prediction, ATNConfigSet configs) {
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
