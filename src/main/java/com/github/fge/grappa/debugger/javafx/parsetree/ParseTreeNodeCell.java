package com.github.fge.grappa.debugger.javafx.parsetree;

import com.github.fge.grappa.debugger.csvtrace.newmodel.ParseTreeNode;
import com.github.fge.grappa.debugger.csvtrace.tabs.tree.TreeTabDisplay;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TreeCell;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

public final class ParseTreeNodeCell
    extends TreeCell<ParseTreeNode>
{
    private final ProgressIndicator indicator = new ProgressIndicator();
    private final Text text = new Text();
    private final HBox hBox = new HBox(text, indicator);

    public ParseTreeNodeCell(final TreeTabDisplay display)
    {
        indicator.setMaxHeight(heightProperty().doubleValue());
        indicator.setVisible(false);
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
    }

    public void showIndicator()
    {
        indicator.setVisible(true);
    }

    public void hideIndicator()
    {
        indicator.setVisible(false);
    }

    @Override
    protected void updateItem(final ParseTreeNode item, final boolean empty)
    {
        super.updateItem(item, empty);
        if (empty) {
            setGraphic(null);
            return;
        }
        final String msg = String.format("%s (%s)",
            item.getRuleInfo().getName(),
            item.isSuccess() ? "SUCCESS" : "FAILURE");
        text.setText(msg);
        setGraphic(hBox);
        ((ParseTreeItem) getTreeItem()).setCell(this);
    }
}

