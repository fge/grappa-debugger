package com.github.fge.grappa.debugger.csvtrace.tabs.tree;

import com.github.fge.grappa.buffers.InputBuffer;
import com.github.fge.grappa.debugger.csvtrace.tabs.TabView;
import com.github.fge.grappa.debugger.model.tabs.tree.ParseTreeNode;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public interface TreeTabView
    extends TabView
{
    void loadInputBuffer(InputBuffer buffer);

    void highlightSuccess(int start, int end);

    void highlightFailure(int end);

    void loadRootNode(ParseTreeNode rootNode);

    void showParseTreeNode(ParseTreeNode node);

    void waitForChildren();

    void setTreeChildren(List<ParseTreeNode> children);
}
