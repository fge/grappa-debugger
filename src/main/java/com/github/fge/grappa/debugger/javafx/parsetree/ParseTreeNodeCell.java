package com.github.fge.grappa.debugger.javafx.parsetree;

import com.github.fge.grappa.debugger.csvtrace.newmodel.ParseTreeNode;
import com.github.fge.grappa.debugger.csvtrace.tabs.tree.TreeTabDisplay;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TreeCell;

public final class ParseTreeNodeCell
    extends TreeCell<ParseTreeNode>
{
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
    }


    @Override
    protected void updateItem(final ParseTreeNode item, final boolean empty)
    {
        super.updateItem(item, empty);
        setText(empty ? null : String.format("%s (%s)",
            item.getRuleInfo().getName(),
            item.isSuccess() ? "SUCCESS" : "FAILURE"));
    }
}

