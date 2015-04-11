package com.github.fge.grappa.debugger.model.tree;

import com.github.fge.grappa.internal.NonFinalForTesting;

@NonFinalForTesting
public class ParseTreeNode
{
    private final int parentId;
    private final int id;
    private final int level;
    private final boolean success;
    private final RuleInfo ruleInfo;
    private final int startIndex;
    private final int endIndex;
    private final long nanos;
    private final boolean hasChildren;

    public ParseTreeNode(final int parentId, final int id, final int level,
        final boolean success, final RuleInfo ruleInfo, final int startIndex,
        final int endIndex, final long nanos, final boolean hasChildren)
    {
        this.parentId = parentId;
        this.id = id;
        this.level = level;
        this.success = success;
        this.ruleInfo = ruleInfo;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.nanos = nanos;
        this.hasChildren = hasChildren;
    }

    public int getParentId()
    {
        return parentId;
    }

    public int getId()
    {
        return id;
    }

    public int getLevel()
    {
        return level;
    }

    public boolean isSuccess()
    {
        return success;
    }

    public RuleInfo getRuleInfo()
    {
        return ruleInfo;
    }

    public int getStartIndex()
    {
        return startIndex;
    }

    public int getEndIndex()
    {
        return endIndex;
    }

    public long getNanos()
    {
        return nanos;
    }

    public boolean hasChildren()
    {
        return hasChildren;
    }
}
