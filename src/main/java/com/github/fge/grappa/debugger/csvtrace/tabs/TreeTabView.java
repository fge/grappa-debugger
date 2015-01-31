package com.github.fge.grappa.debugger.csvtrace.tabs;

import com.github.fge.grappa.buffers.InputBuffer;
import com.github.fge.grappa.debugger.stats.ParseNode;

public interface TreeTabView
{
    void loadTree(ParseNode rootNode);

    void loadText(InputBuffer buffer);

    void showParseNode(ParseNode node);
}
