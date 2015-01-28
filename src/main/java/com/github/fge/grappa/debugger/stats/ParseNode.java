package com.github.fge.grappa.debugger.stats;

import com.github.fge.grappa.internal.NonFinalForTesting;
import com.github.fge.grappa.matchers.MatcherType;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@NonFinalForTesting
public class ParseNode
{
    private final String ruleName;
    private final MatcherType matcherType;
    private final String matcherClass;
    private boolean success;
    private final int start;
    private final int level;

    private int end;
    private long nanos;

    private final List<ParseNode> children = new ArrayList<>();

    public ParseNode(final String ruleName, final MatcherType matcherType,
        final String matcherClass, final int start, final int level)
    {
        this.ruleName = ruleName;
        this.matcherType = matcherType;
        this.matcherClass = matcherClass;
        this.start = start;
        this.level = level;
    }

    void setSuccess(final boolean success)
    {
        this.success = success;
    }

    void setEnd(final int end)
    {
        this.end = end;
    }

    void setNanos(final long nanos)
    {
        this.nanos = nanos;
    }

    public String getRuleName()
    {
        return ruleName;
    }

    public MatcherType getMatcherType()
    {
        return matcherType;
    }

    public String getMatcherClass()
    {
        return matcherClass;
    }

    public boolean isSuccess()
    {
        return success;
    }

    public int getLevel()
    {
        return level;
    }

    public int getStart()
    {
        return start;
    }

    public int getEnd()
    {
        return end;
    }

    public long getNanos()
    {
        return nanos;
    }

    public List<ParseNode> getChildren()
    {
        return Collections.unmodifiableList(children);
    }

    public void addChild(final ParseNode parseNode)
    {
        children.add(parseNode);
    }

    @Override
    @Nonnull
    public String toString()
    {
        return ruleName + " (" + (success ? "SUCCESS" : "FAILURE") + ')';
    }
}
