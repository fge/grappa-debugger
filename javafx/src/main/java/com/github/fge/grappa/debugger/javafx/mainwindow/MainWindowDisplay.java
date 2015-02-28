package com.github.fge.grappa.debugger.javafx.mainwindow;

import com.github.fge.grappa.debugger.javafx.common.JavafxDisplay;
import com.github.fge.grappa.debugger.mainwindow.MainWindowPresenter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;

public class MainWindowDisplay
    extends JavafxDisplay<MainWindowPresenter>
{
    @FXML
    protected BorderPane pane;

    @FXML
    protected Label label;

    @FXML
    protected Label dbLoadStatus;

    @FXML
    protected ProgressBar dbLoadProgress;

    @FXML
    protected Label dbLoadProgressMessage;

    @Override
    public void init()
    {
    }

    @FXML
    void newWindowEvent(final ActionEvent event)
    {
        presenter.handleNewWindow();
    }

    @FXML
    void closeWindowEvent(final ActionEvent event)
    {
        presenter.handleCloseWindow();
    }

    @FXML
    void loadFileEvent(final ActionEvent event)
    {
        presenter.handleLoadFile();
    }
}
