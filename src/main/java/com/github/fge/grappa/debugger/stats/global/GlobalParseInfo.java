package com.github.fge.grappa.debugger.stats.global;

import com.github.fge.grappa.internal.NonFinalForTesting;

@NonFinalForTesting
public class GlobalParseInfo
{
    private final int nonEmptyMatches;
    private final int emptyMatches;
    private final int failedMatches;
    private final int totalMatches;
    private final int treeDepth;
    private final long totalParseTime;

    public GlobalParseInfo(final int nonEmptyMatches, final int emptyMatches,
        final int failedMatches, final int treeDepth, final long totalParseTime)
    {
        this.nonEmptyMatches = nonEmptyMatches;
        this.emptyMatches = emptyMatches;
        this.failedMatches = failedMatches;
        this.treeDepth = treeDepth;
        this.totalParseTime = totalParseTime;
        totalMatches = nonEmptyMatches + emptyMatches + failedMatches;
    }

    public int getNonEmptyMatches()
    {
        return nonEmptyMatches;
    }

    public int getEmptyMatches()
    {
        return emptyMatches;
    }

    public int getFailedMatches()
    {
        return failedMatches;
    }

    public int getTotalMatches()
    {
        return totalMatches;
    }

    public int getTreeDepth()
    {
        return treeDepth;
    }

    public long getTotalParseTime()
    {
        return totalParseTime;
    }
}
