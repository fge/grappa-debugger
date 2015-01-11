package com.github.parboiled1.grappa.debugger.mainwindow;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Pane;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@ParametersAreNonnullByDefault
public class MainWindowUi
{
    private MainWindowPresenter presenter;
    private MainWindowView view;

    @FXML
    Pane pane;

    @FXML
    MenuItem loadInput;
    @FXML
    MenuItem trace;
    @FXML
    MenuItem treeMenuItem;
    @FXML
    MenuItem closeButton;

    @FXML
    TreeView<String> traceTree;
    @FXML
    TextArea traceText;

    @FXML
    TextArea inputText;

    public void init(final MainWindowPresenter presenter,
        final MainWindowView view)
    {
        this.presenter = Objects.requireNonNull(presenter);
        this.view = Objects.requireNonNull(view);
    }

    @FXML
    public void loadFileEvent(final ActionEvent ignored)
    {
        presenter.handleLoadFile();
    }

    @FXML
    public void closeWindowEvent(final ActionEvent ignored)
    {
        presenter.handleCloseWindow();
    }

    @FXML
    public void runTraceEvent(final ActionEvent ignored)
    {
        presenter.handleRunTrace();
    }

    @FXML
    public void treeEvent(final ActionEvent ignored)
    {
        presenter.handleTree();
    }
}
