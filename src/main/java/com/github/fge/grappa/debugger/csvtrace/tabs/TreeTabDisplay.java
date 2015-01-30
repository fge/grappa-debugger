package com.github.fge.grappa.debugger.csvtrace.tabs;

import com.github.fge.grappa.debugger.common.JavafxDisplay;
import com.github.fge.grappa.debugger.stats.ParseNode;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import javafx.scene.text.TextFlow;

public class TreeTabDisplay
    extends JavafxDisplay<TreeTabPresenter>
{
    @FXML
    protected Button treeExpand;

    @FXML
    protected TreeView<ParseNode> parseTree;

    @FXML
    protected TextFlow inputText;

    @Override
    public void init()
    {
        parseTree.setCellFactory(param -> new ParseNodeCell(presenter));
    }

    @FXML
    void expandParseTreeEvent(final Event event)
    {
    }

    private static final class ParseNodeCell
        extends TreeCell<ParseNode>
    {
        private ParseNodeCell(final TreeTabPresenter presenter)
        {
            setEditable(false);
            selectedProperty().addListener(new ChangeListener<Boolean>()
            {
                @Override
                public void changed(
                    final ObservableValue<? extends Boolean> observable,
                    final Boolean oldValue, final Boolean newValue)
                {
                    if (!newValue)
                        return;
                    final ParseNode node = getItem();
                    if (node != null)
                        presenter.parseNodeShowEvent(node);
                }
            });
        }

        @Override
        protected void updateItem(final ParseNode item, final boolean empty)
        {
            super.updateItem(item, empty);
            setText(empty ? null : String.format("%s (%s)", item.getRuleName(),
                item.isSuccess() ? "SUCCESS" : "FAILURE"));
        }
    }
}
