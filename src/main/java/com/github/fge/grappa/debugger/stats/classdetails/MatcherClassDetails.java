package com.github.fge.grappa.debugger.stats.classdetails;

import com.github.fge.grappa.internal.NonFinalForTesting;
import com.github.fge.grappa.matchers.MatcherType;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@NonFinalForTesting
@ParametersAreNonnullByDefault
public final class MatcherClassDetails
{
    private final String className;
    private final MatcherType matcherType;
    private final Set<RuleInvocationDetails> ruleDetails = new HashSet<>();

    public MatcherClassDetails(final String className,
        final MatcherType matcherType)
    {
        this.className = Objects.requireNonNull(className);
        this.matcherType = Objects.requireNonNull(matcherType);
    }

    public String getClassName()
    {
        return className;
    }

    public MatcherType getMatcherType()
    {
        return matcherType;
    }

    public int getNonEmptyMatches()
    {
        return ruleDetails.parallelStream()
            .mapToInt(RuleInvocationDetails::getNonEmptyMatches).sum();
    }

    public int getEmptyMatches()
    {
        return ruleDetails.parallelStream()
            .mapToInt(RuleInvocationDetails::getEmptyMatches).sum();
    }

    public int getFailedMatches()
    {
        return ruleDetails.parallelStream()
            .mapToInt(RuleInvocationDetails::getFailedMatches).sum();
    }

    public Set<RuleInvocationDetails> getRuleDetails()
    {
        return Collections.unmodifiableSet(ruleDetails);
    }

    void addRuleInvocationDetails(final RuleInvocationDetails details)
    {
        ruleDetails.add(Objects.requireNonNull(details));
    }
}
