package com.github.fge.grappa.debugger.csvtrace.tabs;

import com.github.fge.grappa.debugger.stats.ParseNode;

public interface TreeTabView
{
    void loadTree(ParseNode rootNode);

    void loadText();

    void showParseNode(ParseNode node);

    void highlightSuccess(int start, int end);

    void highlightFailure(int end);
}
