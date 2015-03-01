package com.github.fge.grappa.debugger.javafx.custom;

import com.github.fge.grappa.debugger.javafx.csvtrace.tabs.tree.TreeTabDisplay;
import com.github.fge.grappa.debugger.model.tabs.tree.ParseTreeNode;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
        expandedProperty().addListener(new ChangeListener<Boolean>()
        {
            @Override
            public void changed(
                final ObservableValue<? extends Boolean> observable,
                final Boolean oldValue, final Boolean newValue)
            {
                if (!newValue) {
                    getChildren().clear();
                    return;
                }
                display.needChildrenEvent(ParseTreeItem.this);
            }
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
