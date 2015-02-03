package com.github.fge.grappa.debugger.javafx.parsetree;

import com.github.fge.grappa.debugger.csvtrace.newmodel.ParseTreeNode;
import com.github.fge.grappa.debugger.csvtrace.tabs.tree.TreeTabDisplay;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

import java.util.List;
import java.util.concurrent.ExecutionException;

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
            final List<ParseTreeNode> nodes;
            try {
                nodes = display.getNodeChildren(getValue().getId());
            } catch (ExecutionException ignored) {
                return ret;
            }
            nodes.stream().map(
                node -> new ParseTreeItem(display, node)).forEach(ret::add);
            childrenLoaded = true;
        }
        return ret;
    }
}
