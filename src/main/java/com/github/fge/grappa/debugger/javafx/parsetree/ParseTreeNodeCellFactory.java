package com.github.fge.grappa.debugger.javafx.parsetree;

import com.github.fge.grappa.debugger.csvtrace.newmodel.ParseTreeNode;
import com.github.fge.grappa.debugger.csvtrace.tabs.tree.TreeTabDisplay;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import javafx.util.Callback;

public final class ParseTreeNodeCellFactory
    implements Callback<TreeView<ParseTreeNode>, TreeCell<ParseTreeNode>>
{
    private final TreeTabDisplay display;

    public ParseTreeNodeCellFactory(final TreeTabDisplay display)
    {
        this.display = display;
    }

    @Override
    public TreeCell<ParseTreeNode> call(final TreeView<ParseTreeNode> param)
    {
        return new ParseTreeNodeCell(display);
    }
}
