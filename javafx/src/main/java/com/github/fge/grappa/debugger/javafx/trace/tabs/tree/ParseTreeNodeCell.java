package com.github.fge.grappa.debugger.javafx.trace.tabs.tree;

import com.github.fge.grappa.debugger.model.tree.ParseTreeNode;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TreeCell;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import org.fxmisc.easybind.EasyBind;

public final class ParseTreeNodeCell
    extends TreeCell<ParseTreeNode>
{
    private final Text text = new Text();
    private final ProgressBar progressBar = new ProgressBar();
    private final HBox hBox = new HBox(text);

    public ParseTreeNodeCell(final TreeTabDisplay display)
    {
        setEditable(false);

        selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue)
                return;
            final ParseTreeNode node = getItem();
            if (node != null)
                display.parseTreeNodeShowEvent(node);
        });

        final ObservableValue<Boolean> loading
            = EasyBind.select(treeItemProperty())
            .selectObject(item -> ((ParseTreeItem) item).loadingProperty());

        loading.addListener(new ChangeListener<Boolean>()
        {
            @Override
            public void changed(
                final ObservableValue<? extends Boolean> observable,
                final Boolean oldValue, final Boolean newValue)
            {
                final ObservableList<Node> children = hBox.getChildren();
                if (newValue == null || !newValue) {
                    children.remove(progressBar);
                    return;
                }

                if (!children.contains(progressBar))
                    children.add(progressBar);
            }
        });

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
                text.setText(String.format("%s (%s)",
                    newValue.getRuleInfo().getName(),
                    newValue.isSuccess() ? "SUCCESS" : "FAILURE"));
                setGraphic(hBox);
            }
        });
    }
}

