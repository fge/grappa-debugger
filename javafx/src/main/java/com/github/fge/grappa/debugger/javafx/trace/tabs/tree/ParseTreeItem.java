package com.github.fge.grappa.debugger.javafx.trace.tabs.tree;

import com.github.fge.grappa.debugger.model.tree.ParseTreeNode;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.TreeItem;

public final class ParseTreeItem
    extends TreeItem<ParseTreeNode>
{
    private final BooleanProperty loadingProperty
        = new SimpleBooleanProperty(false);

    private final boolean leaf;

    public ParseTreeItem(final TreeTabDisplay display,
        final ParseTreeNode value)
    {
        super(value);
        leaf = !value.hasChildren();
        expandedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                getChildren().clear();
                return;
            }
            display.needChildrenEvent(this);
        });
    }

    public BooleanProperty loadingProperty()
    {
        return loadingProperty;
    }

    @Override
    public boolean isLeaf()
    {
        return leaf;
    }
}
