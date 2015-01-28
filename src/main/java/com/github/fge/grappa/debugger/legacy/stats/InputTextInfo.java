package com.github.fge.grappa.debugger.legacy.stats;

import com.github.fge.grappa.buffers.InputBuffer;
import com.github.fge.grappa.internal.NonFinalForTesting;

@NonFinalForTesting
public class InputTextInfo
{
    private final int nrLines;
    private final int nrChars;
    private final int nrCodePoints;

    public InputTextInfo(final InputBuffer buffer)
    {
        nrLines = buffer.getLineCount();
        nrChars = buffer.length();
        nrCodePoints = buffer.extract(0, buffer.length())
            .codePointCount(0, nrChars);
    }

    public int getNrLines()
    {
        return nrLines;
    }

    public int getNrChars()
    {
        return nrChars;
    }

    public int getNrCodePoints()
    {
        return nrCodePoints;
    }
}
