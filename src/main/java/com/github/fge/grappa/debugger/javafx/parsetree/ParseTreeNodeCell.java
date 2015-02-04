package com.github.fge.grappa.debugger.javafx.parsetree;

import com.github.fge.grappa.debugger.csvtrace.newmodel.ParseTreeNode;
import com.github.fge.grappa.debugger.csvtrace.tabs.tree.TreeTabDisplay;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

public final class ParseTreeNodeCell
    extends TreeCell<ParseTreeNode>
{
    private final Text text = new Text();
    private final ProgressBar bar = new ProgressBar();
    final HBox hBox = new HBox();

    public ParseTreeNodeCell(final TreeTabDisplay display)
    {
        setEditable(false);
        selectedProperty().addListener(new ChangeListener<Boolean>()
        {
            @SuppressWarnings("AutoUnboxing")
            @Override
            public void changed(
                final ObservableValue<? extends Boolean> observable,
                final Boolean oldValue, final Boolean newValue)
            {
                if (!newValue)
                    return;
                final ParseTreeNode node = getItem();
                if (node != null)
                    display.parseTreeNodeShowEvent(node);
            }
        });

        final ChangeListener<TreeItem<ParseTreeNode>> listener
            = new ChangeListener<TreeItem<ParseTreeNode>>()
        {
            @Override
            public void changed(
                final ObservableValue<? extends TreeItem<ParseTreeNode>> unused,
                final TreeItem<ParseTreeNode> oldValue,
                final TreeItem<ParseTreeNode> newValue)
            {
                // FIXME: this cast is needed, unfortunately :(
                final ParseTreeItem treeItem = (ParseTreeItem) newValue;

                setText(null);
                
                if (treeItem == null) {
                    setGraphic(null);
                    return;
                }

                final ParseTreeNode item = treeItem.getValue();

                final String msg = String.format("%s (%s)",
                    item.getRuleInfo().getName(),
                    item.isSuccess() ? "SUCCESS" : "FAILURE");
                text.setText(msg);

                final ObservableList<Node> children = hBox.getChildren();
                children.clear();
                children.add(text);

                final boolean loadInProgress
                    = treeItem.waitingChildrenProperty().get();
                if (loadInProgress)
                    children.add(bar);
                setGraphic(hBox);
            }
        };

        treeItemProperty().addListener(listener);
    }

    @Override
    protected void updateItem(final ParseTreeNode item, final boolean empty)
    {
        super.updateItem(item, empty);
        setText(null);
        if (empty || item == null)
            setGraphic(null);
    }
}

