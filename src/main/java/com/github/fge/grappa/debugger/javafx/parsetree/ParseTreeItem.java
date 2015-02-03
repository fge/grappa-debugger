package com.github.fge.grappa.debugger.javafx.parsetree;

import com.github.fge.grappa.debugger.csvtrace.newmodel.ParseTreeNode;
import com.github.fge.grappa.debugger.csvtrace.tabs.tree.TreeTabDisplay;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TreeItem;

public final class ParseTreeItem
    extends TreeItem<ParseTreeNode>
{
    private final boolean leaf;

    private ParseTreeNodeCell cell;

    public ParseTreeItem(final TreeTabDisplay display,
        final ParseTreeNode value)
    {
        super(value);
        leaf = !value.hasChildren();
        expandedProperty().addListener(new ChangeListener<Boolean>()
        {
            @Override
            public void changed(
                final ObservableValue<? extends Boolean> observable,
                final Boolean oldValue, final Boolean newValue)
            {
                if (oldValue == newValue)
                    return;
                if (!newValue) {
                    getChildren().clear();
                    return;
                }
                display.needChildren(ParseTreeItem.this);
            }
        });
    }

    @Override
    public boolean isLeaf()
    {
        return leaf;
    }

    public void setCell(final ParseTreeNodeCell cell)
    {
        this.cell = cell;
    }

    public void showIndicator()
    {
        cell.showIndicator();
    }

    public void hideIndicator()
    {
        cell.hideIndicator();
    }
}
