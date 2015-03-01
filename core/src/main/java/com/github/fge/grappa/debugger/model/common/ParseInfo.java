package com.github.fge.grappa.debugger.model.common;

import java.time.LocalDateTime;

public class ParseInfo
{
    private final LocalDateTime time;
    private final int treeDepth;
    private final int nrMatchers;
    private final int nrLines;
    private final int nrChars;
    private final int nrCodePoints;
    private final int nrInvocations;

    public ParseInfo(final LocalDateTime time, final int treeDepth,
        final int nrMatchers, final int nrLines, final int nrChars,
        final int nrCodePoints, final int nrInvocations)
    {
        this.time = time;
        this.treeDepth = treeDepth;
        this.nrMatchers = nrMatchers;
        this.nrLines = nrLines;
        this.nrChars = nrChars;
        this.nrCodePoints = nrCodePoints;
        this.nrInvocations = nrInvocations;
    }

    public LocalDateTime getTime()
    {
        return time;
    }

    public int getTreeDepth()
    {
        return treeDepth;
    }

    public int getNrMatchers()
    {
        return nrMatchers;
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

    public int getNrInvocations()
    {
        return nrInvocations;
    }
}
