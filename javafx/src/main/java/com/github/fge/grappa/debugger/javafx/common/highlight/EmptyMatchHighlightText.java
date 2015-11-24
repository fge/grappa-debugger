package com.github.fge.grappa.debugger.javafx.common.highlight;

import com.github.fge.grappa.buffers.InputBuffer;
import com.github.fge.grappa.debugger.javafx.common.JavafxUtils;

public final class EmptyMatchHighlightText
    extends MatchHighlightText
{
    public EmptyMatchHighlightText(final InputBuffer buffer, final int index)
    {
        super(buffer, index, index, JavafxUtils.CSS_STYLE_MATCHSUCCESS);
    }

    @Override
    protected String decoratedMatchText()
    {
        return JavafxUtils.MATCH_EMPTY;
    }
}
