package com.github.fge.grappa.debugger.model.tree;

import com.github.fge.grappa.internal.NonFinalForTesting;
import com.github.fge.grappa.matchers.MatcherType;

@NonFinalForTesting
public class RuleInfo
{
    private final String className;
    private final MatcherType type;
    private final String name;

    public RuleInfo(final String className, final MatcherType type,
        final String name)
    {
        this.className = className;
        this.type = type;
        this.name = name;
    }

    public String getClassName()
    {
        return className;
    }

    public MatcherType getType()
    {
        return type;
    }

    public String getName()
    {
        return name;
    }
}
