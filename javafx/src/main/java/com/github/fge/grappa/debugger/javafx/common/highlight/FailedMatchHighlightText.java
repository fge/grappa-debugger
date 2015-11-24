package com.github.fge.grappa.debugger.javafx.common.highlight;

import com.github.fge.grappa.buffers.InputBuffer;
import com.github.fge.grappa.debugger.javafx.common.JavafxUtils;

public final class FailedMatchHighlightText
    extends MatchHighlightText
{
    public FailedMatchHighlightText(final InputBuffer buffer, final int index)
    {
        super(buffer, index, index, JavafxUtils.CSS_STYLE_MATCHFAILURE);
    }

    @Override
    protected String decoratedMatchText()
    {
        return JavafxUtils.MATCH_FAILURE;
    }
}
