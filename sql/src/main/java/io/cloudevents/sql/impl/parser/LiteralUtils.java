package io.cloudevents.sql.impl.parser;

import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.regex.Pattern;

public class LiteralUtils {

    public static String parseSQuotedStringLiteral(TerminalNode node) {
        String val = node.getText();
        val = val.substring(1, val.length() - 1);
        val = val.replaceAll(Pattern.quote("\\'"), "'");
        return val;
    }

    public static String parseDQuotedStringLiteral(TerminalNode node) {
        String val = node.getText();
        val = val.substring(1, val.length() - 1);
        val = val.replaceAll(Pattern.quote("\\\""), "\"");
        return val;
    }

}
