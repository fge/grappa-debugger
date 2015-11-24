package com.github.fge.grappa.debugger.javafx.common.highlight;

import com.github.fge.grappa.buffers.InputBuffer;
import com.github.fge.grappa.debugger.javafx.common.JavafxUtils;

public final class NonemptyMatchHighlightText
    extends MatchHighlightText
{
    public NonemptyMatchHighlightText(final InputBuffer buffer,
        final int startIndex, final int endIndex)
    {
        super(buffer, startIndex, endIndex, JavafxUtils.CSS_STYLE_MATCHSUCCESS);
    }

    @Override
    protected String decoratedMatchText()
    {
        return JavafxUtils.MATCH_BEFORE + matchedText()
            + JavafxUtils.MATCH_AFTER;
    }
}
