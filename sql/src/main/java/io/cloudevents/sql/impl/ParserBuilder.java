package io.cloudevents.sql.impl;

import io.cloudevents.sql.Parser;

public class ParserBuilder {

    private boolean constantFolding;

    public ParserBuilder() {
        this.constantFolding = true;
    }

    /**
     * Disable constant folding when parsing.
     *
     * @return this
     */
    public ParserBuilder disableConstantFolding() {
        this.constantFolding = false;
        return this;
    }

    /**
     * @return the new {@link Parser}
     */
    public Parser build() {
        return new ParserImpl(this.constantFolding);
    }

}
