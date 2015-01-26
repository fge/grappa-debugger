package com.github.fge.grappa.debugger.legacy.stats;

public final class RuleStatistics
{
    private final String ruleName;
    private int nrInvocations = 0;
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

    public int getNrSuccesses()
    {
        return nrSuccesses;
    }

    public void addInvocation(final boolean success)
    {
        nrInvocations++;
        if (success)
            nrSuccesses++;
    }
}
