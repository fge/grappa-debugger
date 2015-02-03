package com.github.fge.grappa.debugger.csvtrace.tabs.tree;

import com.github.fge.grappa.debugger.csvtrace.newmodel.InputText;
import com.github.fge.grappa.debugger.csvtrace.newmodel.ParseTree;
import com.github.fge.grappa.debugger.csvtrace.newmodel.ParseTreeNode;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface TreeTabView
{
    void loadInputText(InputText inputText);

    void highlightSuccess(int start, int end);

    void highlightFailure(int end);

    void loadParseTree(@Nullable ParseTree parseTree);

    void showParseTreeNode(ParseTreeNode node);

}
