package com.github.parboiled1.grappa.debugger.tracetab.statistics;

import com.github.parboiled1.grappa.buffers.InputBuffer;

public final class InputTextInfo
{
    private final String contents;
    private final int nrLines;
    private final int nrChars;
    private final int nrCodePoints;

    public InputTextInfo(final InputBuffer buffer)
    {
        nrLines = buffer.getLineCount();
        nrChars = buffer.length();
        contents = buffer.extract(0, nrChars);
        nrCodePoints = contents.codePointCount(0, nrChars);
    }

    public String getContents()
    {
        return contents;
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
