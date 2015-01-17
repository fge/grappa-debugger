package com.github.parboiled1.grappa.debugger.tracetab.statistics;

public final class RuleStatistics
{
    private final String ruleName;
    private int nrInvocations = 0;
    private long totalTime = 0L;
    private int nrSuccesses = 0;

    public RuleStatistics(final String ruleName)
    {
        this.ruleName = ruleName;
    }

    public String getRuleName()
    {
        return ruleName;
    }

    public int getNrInvocations()
    {
        return nrInvocations;
    }

    public long getTotalTime()
    {
        return totalTime;
    }

    public int getNrSuccesses()
    {
        return nrSuccesses;
    }

    public void addInvocation(final long time, final boolean success)
    {
        nrInvocations++;
        totalTime += time;
        if (success)
            nrSuccesses++;
    }
}
