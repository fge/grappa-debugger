package com.github.fge.grappa.debugger.csvtrace.tabs.tree;

import com.github.fge.grappa.debugger.csvtrace.tabs.TabView;
import com.github.fge.grappa.debugger.model.tabs.tree.InputText;
import com.github.fge.grappa.debugger.model.tabs.tree.ParseTree;
import com.github.fge.grappa.debugger.model.tabs.tree.ParseTreeNode;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public interface TreeTabView
    extends TabView
{
    void loadInputText(InputText inputText);

    void highlightSuccess(int start, int end);

    void highlightFailure(int end);

    void loadParseTree(@Nullable ParseTree parseTree);

    void showParseTreeNode(ParseTreeNode node);

    void waitForChildren();

    void setTreeChildren(List<ParseTreeNode> children);
}
