package com.github.fge.grappa.debugger.csvtrace.newmodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ParseTreeNode
{
    private final int parentId;
    private final int id;
    private final int level;
    private final boolean success;
    private final int matcherId;
    private final int startIndex;
    private final int endIndex;
    private final long nanos;

    private final List<ParseTreeNode> children = new ArrayList<>();

    ParseTreeNode(final int parentId, final int id, final int level,
        final boolean success, final int matcherId, final int startIndex,
        final int endIndex, final long nanos)
    {
        this.parentId = parentId;
        this.id = id;
        this.level = level;
        this.success = success;
        this.matcherId = matcherId;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.nanos = nanos;
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

    public int getMatcherId()
    {
        return matcherId;
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

    public List<ParseTreeNode> getChildren()
    {
        return Collections.unmodifiableList(children);
    }

    void addChild(final ParseTreeNode child)
    {
        children.add(child);
    }
}