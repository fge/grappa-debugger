package com.github.parboiled1.grappa.debugger.tracetab.statistics;

import com.github.parboiled1.grappa.debugger.internal.NonFinalForTesting;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@NonFinalForTesting
public class ParseNode
{
    private final String ruleName;
    private boolean success;
    private final int start;
    private int end;

    private final List<ParseNode> children = new ArrayList<>();

    public ParseNode(final String ruleName, final int start)
    {
        this.ruleName = ruleName;
        this.start = start;
    }

    void setSuccess(final boolean success)
    {
        this.success = success;
    }

    void setEnd(final int end)
    {
        this.end = end;
    }

    public String getRuleName()
    {
        return ruleName;
    }

    public boolean isSuccess()
    {
        return success;
    }

    public int getStart()
    {
        return start;
    }

    public int getEnd()
    {
        return end;
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
