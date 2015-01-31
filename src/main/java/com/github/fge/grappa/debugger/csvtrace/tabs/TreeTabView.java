package com.github.fge.grappa.debugger.csvtrace.tabs;

import com.github.fge.grappa.debugger.stats.ParseNode;
import com.github.fge.grappa.trace.ParseRunInfo;

public interface TreeTabView
{
    void loadTree(ParseNode rootNode);

    void loadText();

    void showParseNode(ParseNode node);

    void highlightSuccess(int start, int end);

    void highlightFailure(int end);

    void loadParseRunInfo(ParseRunInfo info);

    void expandParseTree();
}
