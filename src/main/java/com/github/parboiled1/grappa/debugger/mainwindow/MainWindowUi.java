package com.github.parboiled1.grappa.debugger.mainwindow;

import com.github.parboiled1.grappa.debugger.parser.MatchResult;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Pane;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@ParametersAreNonnullByDefault
public class MainWindowUi
{
    private MainWindowPresenter presenter;

    @FXML
    Pane pane;

    @FXML
    MenuItem loadInput;
    @FXML
    MenuItem parse;
    @FXML
    MenuItem closeButton;

    @FXML
    TreeView<MatchResult> traceTree;
    @FXML
    TextArea traceDetail;

    @FXML
    TextArea inputText;

    public void init(final MainWindowPresenter presenter)
    {
        this.presenter = Objects.requireNonNull(presenter);
    }

    @FXML
    public void loadFileEvent(final ActionEvent ignored)
    {
        presenter.handleLoadFile();
    }

    @FXML
    public void parseEvent(final ActionEvent ignored)
    {
        presenter.handleParse();
    }

    @FXML
    public void closeWindowEvent(final ActionEvent ignored)
    {
        presenter.handleCloseWindow();
    }

    public void matchResultShowEvent(final TreeItem<MatchResult> item)
    {
        presenter.handleMatchResultShow(item);
    }
}
