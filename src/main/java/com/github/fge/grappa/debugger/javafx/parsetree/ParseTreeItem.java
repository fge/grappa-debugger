package com.github.fge.grappa.debugger.javafx.parsetree;

import com.github.fge.grappa.debugger.csvtrace.newmodel.ParseTreeNode;
import com.github.fge.grappa.debugger.csvtrace.tabs.tree.TreeTabDisplay;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

public final class ParseTreeItem
    extends TreeItem<ParseTreeNode>
{
    private final TreeTabDisplay display;
    private final boolean leaf;

    private boolean childrenLoaded = false;

    public ParseTreeItem(final TreeTabDisplay display,
        final ParseTreeNode value)
    {
        super(value);
        this.display = display;
        leaf = !value.hasChildren();
    }

    @Override
    public boolean isLeaf()
    {
        return leaf;
    }

    @Override
    public ObservableList<TreeItem<ParseTreeNode>> getChildren()
    {
        final ObservableList<TreeItem<ParseTreeNode>> ret = super.getChildren();
        if (!childrenLoaded) {
            display.getNodeChildren(getValue().getId()).stream().map(
                node -> new ParseTreeItem(display, node)).forEach(ret::add);
            childrenLoaded = true;
        }
        return ret;
    }
}
