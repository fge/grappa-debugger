package com.github.fge.grappa.debugger.model.tabs.tree;

import com.github.fge.grappa.internal.NonFinalForTesting;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@NonFinalForTesting
@ParametersAreNonnullByDefault
public class ParseTree
{
    private final ParseTreeNode rootNode;
    private final int nrInvocations;
    private final int treeDepth;

    public ParseTree(final ParseTreeNode rootNode, final int nrInvocations,
        final int treeDepth)
    {
        this.rootNode = Objects.requireNonNull(rootNode);
        this.nrInvocations = nrInvocations;
        this.treeDepth = treeDepth;
    }

    @Nonnull
    public ParseTreeNode getRootNode()
    {
        return rootNode;
    }

    public int getNrInvocations()
    {
        return nrInvocations;
    }

    public int getTreeDepth()
    {
        return treeDepth;
    }
}
