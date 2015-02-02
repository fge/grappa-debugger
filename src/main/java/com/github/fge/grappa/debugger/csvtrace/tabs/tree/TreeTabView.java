package com.github.fge.grappa.debugger.csvtrace.tabs.tree;

import com.github.fge.grappa.buffers.InputBuffer;
import com.github.fge.grappa.debugger.csvtrace.newmodel.ParseTreeNode;

public interface TreeTabView
{
    void waitForText();

    void loadText(InputBuffer inputBuffer);

    void highlightSuccess(int start, int end);

    void highlightFailure(int end);

    void waitForTree();

    void loadTree(ParseTreeNode rootNode);

    void showParseTreeNode(ParseTreeNode node);

}
