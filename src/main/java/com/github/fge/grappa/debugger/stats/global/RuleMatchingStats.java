package com.github.fge.grappa.debugger.stats.global;

import com.github.fge.grappa.matchers.MatcherType;

public final class RuleMatchingStats
{
    private final String ruleName;
    private final String matcherClass;
    private final MatcherType matcherType;
    private int nonEmptyMatches = 0;
    private int emptyMatches = 0;
    private int failures = 0;

    public RuleMatchingStats(final String ruleName, final String matcherClass,
        final MatcherType matcherType)
    {
        this.ruleName = ruleName;
        this.matcherClass = matcherClass;
        this.matcherType = matcherType;
    }

    public String getRuleName()
    {
        return ruleName;
    }

    public String getMatcherClass()
    {
        return matcherClass;
    }

    public MatcherType getMatcherType()
    {
        return matcherType;
    }

    public int getNonEmptyMatches()
    {
        return nonEmptyMatches;
    }

    public int getEmptyMatches()
    {
        return emptyMatches;
    }

    void addMatch(final boolean empty)
    {
        if (empty)
            emptyMatches++;
        else
            nonEmptyMatches++;
    }

    public int getFailures()
    {
        return failures;
    }

    void addFailure()
    {
        failures++;
    }
}
