package com.github.parboiled1.grappa.debugger.mainwindow.parsetree;

import com.github.parboiled1.grappa.debugger.mainwindow.MainWindowPresenter;
import com.github.parboiled1.grappa.debugger.parser.MatchResult;
import javafx.event.EventHandler;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

public final class ParseNodeCellFactory
    implements Callback<TreeView<MatchResult>, TreeCell<MatchResult>>
{
    private final MainWindowPresenter presenter;

    public ParseNodeCellFactory(final MainWindowPresenter presenter)
    {
        this.presenter = presenter;
    }

    @Override
    public TreeCell<MatchResult> call(final TreeView<MatchResult> param)
    {
        final ParsingTreeCell cell = new ParsingTreeCell();
        final EventHandler<MouseEvent> handler
            = event -> presenter.handleMatchResult(cell.getTreeItem());
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
            if (!empty) {
                final String text = item.getLabel() + " ("
                    + (item.isSuccess() ? "SUCCESS" : "FAILURE") + ')';
                setText(text);
                setGraphic(getTreeItem().getGraphic());
            } else {
                setText(null);
                setGraphic(null);
            }
        }
    }
}
