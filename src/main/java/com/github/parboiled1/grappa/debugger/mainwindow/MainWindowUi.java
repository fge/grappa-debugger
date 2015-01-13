package com.github.parboiled1.grappa.debugger.mainwindow;

import com.github.parboiled1.grappa.debugger.internal.NonFinalForTesting;
import com.github.parboiled1.grappa.debugger.internal.NotFXML;
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
@NonFinalForTesting
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

    @NotFXML
    public void init(final MainWindowPresenter presenter)
    {
        this.presenter = Objects.requireNonNull(presenter);
    }

    @FXML
    void loadFileEvent(final ActionEvent ignored)
    {
        presenter.handleLoadFile();
    }

    @FXML
    void parseEvent(final ActionEvent ignored)
    {
        presenter.handleParse();
    }

    @FXML
    void closeWindowEvent(final ActionEvent ignored)
    {
        presenter.handleCloseWindow();
    }

    // TODO: handle that better... See ParseNodeCellFactory
    @NotFXML
    public void matchResultShowEvent(final TreeItem<MatchResult> item)
    {
        presenter.handleMatchResultShow(item);
    }
}
