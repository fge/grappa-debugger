package com.github.fge.grappa.debugger.javafx.parsetree;

import com.github.fge.grappa.debugger.csvtrace.newmodel.ParseTreeNode;
import com.github.fge.grappa.debugger.csvtrace.tabs.tree.TreeTabDisplay;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TreeCell;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import org.fxmisc.easybind.EasyBind;

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
                if (newValue == null || !newValue.booleanValue()) {
                    children.remove(bar);
                    return;
                }

                if (!children.contains(bar))
                    children.add(bar);
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

