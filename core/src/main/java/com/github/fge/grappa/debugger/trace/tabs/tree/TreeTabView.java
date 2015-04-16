package com.github.fge.grappa.debugger.trace.tabs.tree;

import com.github.fge.grappa.buffers.InputBuffer;
import com.github.fge.grappa.debugger.model.tree.ParseTreeNode;
import com.github.fge.grappa.debugger.trace.tabs.TabView;

import java.util.List;

public interface TreeTabView
    extends TabView
{
    void loadInputBuffer(InputBuffer buffer);

    void displayTree(ParseTreeNode node);

    void highlightSuccess(int start, int end);

    void highlightFailure(int end);

    void showParseTreeNode(ParseTreeNode node);

    void waitForChildren();

    void setTreeChildren(List<ParseTreeNode> children);
}
