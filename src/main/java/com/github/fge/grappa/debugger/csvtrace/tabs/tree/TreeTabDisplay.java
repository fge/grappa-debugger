package com.github.fge.grappa.debugger.csvtrace.tabs.tree;

import com.github.fge.grappa.debugger.common.JavafxDisplay;
import com.github.fge.grappa.debugger.csvtrace.newmodel.ParseTreeNode;
import com.github.fge.grappa.debugger.stats.ParseNode;
import com.google.common.annotations.VisibleForTesting;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToolBar;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import javafx.scene.text.TextFlow;

public class TreeTabDisplay
    extends JavafxDisplay<TreeTabPresenter>
{
    /*
     * Tree
     */
    @FXML
    protected ToolBar treeToolbar;

    @FXML
    protected Button treeExpand;

    @FXML
    protected Label treeLoading;

    @FXML
    protected TreeView<ParseNode> parseTree;

    @FXML
    protected TreeView<ParseTreeNode> parseTree2;

    /*
     * Node detail
     */
    @FXML
    protected Label nodeDepth;

    @FXML
    protected Label nodeRuleName;

    @FXML
    protected Label nodeMatcherType;

    @FXML
    protected Label nodeMatcherClass;

    @FXML
    protected Label nodeStatus;

    @FXML
    protected Label nodeStartPos;

    @FXML
    protected Label nodeEndPos;

    @FXML
    protected Label nodeTime;

    /*
     * Text
     */
    @FXML
    protected Label textInfo;

    @FXML
    protected TextFlow inputText;

    @FXML
    protected ScrollPane inputTextScroll;

    @Override
    public void init()
    {
        parseTree.setCellFactory(param -> new ParseNodeCell(this));
        parseTree2.setCellFactory(param -> new ParseTreeNodeCell(this));
    }

    @FXML
    void expandParseTreeEvent(final Event event)
    {
        presenter.handleExpandParseTree();
    }

    private static final class ParseNodeCell
        extends TreeCell<ParseNode>
    {
        private ParseNodeCell(final TreeTabDisplay display)
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
                        display.parseNodeShowEvent(node);
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

    private static final class ParseTreeNodeCell
        extends TreeCell<ParseTreeNode>
    {
        private ParseTreeNodeCell(final TreeTabDisplay display)
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

    @VisibleForTesting
    void parseTreeNodeShowEvent(final ParseTreeNode node)
    {
        presenter.handleParseTreeNodeShow(node);
    }


    @VisibleForTesting
    void parseNodeShowEvent(final ParseNode node)
    {
        presenter.handleParseNodeShow(node);
    }
}
