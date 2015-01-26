package com.github.fge.grappa.debugger.stats.classdetails;

public final class RuleInvocationDetails
{
    private final String ruleName;
    int nonEmptyMatches = 0;
    int emptyMatches = 0;
    int failedMatches = 0;

    RuleInvocationDetails(final String ruleName)
    {
        this.ruleName = ruleName;
    }

    public String getRuleName()
    {
        return ruleName;
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
}
