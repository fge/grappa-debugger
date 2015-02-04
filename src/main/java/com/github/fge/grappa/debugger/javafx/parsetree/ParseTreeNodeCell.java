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

import javax.annotation.Nullable;

public final class ParseTreeNodeCell
    extends TreeCell<ParseTreeNode>
{
    private final Text text = new Text();
    private final ProgressBar bar = new ProgressBar();
    final HBox hBox = new HBox(text);

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
                if (newValue == null)
                    return;

                final ParseTreeItem item = (ParseTreeItem) newValue;

                final ObservableList<Node> children = hBox.getChildren();
                final String txt = stringValue(newValue.getValue());
                text.setText(txt);
                if (!item.loadingProperty().get()) {
                    children.remove(bar);
                    return;
                }
                if (!children.contains(bar))
                    children.add(bar);
            }
        };

        treeItemProperty().addListener(listener);

        itemProperty().addListener(new ChangeListener<ParseTreeNode>()
        {
            @Override
            public void changed(
                final ObservableValue<? extends ParseTreeNode> observable,
                final ParseTreeNode oldValue, final ParseTreeNode newValue)
            {
                if (newValue == null) {
                    setGraphic(null);
                    return;
                }
                text.setText(stringValue(newValue));
                setGraphic(hBox);
            }
        });
    }

    @Nullable
    private static String stringValue(@Nullable final ParseTreeNode node)
    {
        return node == null ? null
            : String.format("%s (%s)", node.getRuleInfo().getName(),
                node.isSuccess() ? "SUCCESS" : "FAILURE");
    }
}

