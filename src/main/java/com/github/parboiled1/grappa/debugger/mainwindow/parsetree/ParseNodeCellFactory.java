package com.github.parboiled1.grappa.debugger.mainwindow.parsetree;

import com.github.parboiled1.grappa.debugger.mainwindow.MainWindowPresenter;
import com.github.parboiled1.grappa.debugger.mainwindow.MainWindowUi;
import com.github.parboiled1.grappa.debugger.parser.MatchResult;
import javafx.event.EventHandler;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

public final class ParseNodeCellFactory
    implements Callback<TreeView<MatchResult>, TreeCell<MatchResult>>
{
    private final MainWindowUi ui;

    public ParseNodeCellFactory(final MainWindowUi ui)
    {
        this.ui = ui;
    }

    @Override
    public TreeCell<MatchResult> call(final TreeView<MatchResult> param)
    {
        final ParsingTreeCell cell = new ParsingTreeCell();
        final EventHandler<MouseEvent> handler
            = event -> ui.matchResultShowEvent(cell.getTreeItem());
        cell.setOnMouseClicked(handler);
        return cell;
    }

    private static final class ParsingTreeCell
        extends TreeCell<MatchResult>
    {
        @Override
        protected void updateItem(final MatchResult item, final boolean empty)
        {
            super.updateItem(item, empty);
            final String text = empty ? null : String.format("%s (%s)",
                item.getLabel(), item.isSuccess() ? "SUCCESS": "FAILURE");
            setText(text);
        }
    }
}
