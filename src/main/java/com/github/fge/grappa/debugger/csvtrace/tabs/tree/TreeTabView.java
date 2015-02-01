package com.github.fge.grappa.debugger.csvtrace.tabs.tree;

import com.github.fge.grappa.debugger.csvtrace.newmodel.ParseTreeNode;
import com.github.fge.grappa.trace.ParseRunInfo;

public interface TreeTabView
{
    void loadText();

    void highlightSuccess(int start, int end);

    void highlightFailure(int end);

    void loadParseRunInfo(ParseRunInfo info);

    void expandParseTree();

    void loadTree2(ParseTreeNode rootNode);

    void showParseTreeNode(ParseTreeNode node);
}
