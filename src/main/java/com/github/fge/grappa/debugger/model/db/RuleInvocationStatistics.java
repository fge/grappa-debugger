package com.github.fge.grappa.debugger.model.db;

import com.github.fge.grappa.debugger.model.RuleInfo;

public final class RuleInvocationStatistics
{
    private final RuleInfo ruleInfo;
    private final int nonEmptyMatches;
    private final int emptyMatches;
    private final int failedMatches;

    public RuleInvocationStatistics(final RuleInfo ruleInfo,
        final int nonEmptyMatches, final int emptyMatches,
        final int failedMatches)
    {
        this.ruleInfo = ruleInfo;
        this.nonEmptyMatches = nonEmptyMatches;
        this.emptyMatches = emptyMatches;
        this.failedMatches = failedMatches;
    }

    public RuleInfo getRuleInfo()
    {
        return ruleInfo;
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
