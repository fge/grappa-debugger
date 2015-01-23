package com.github.fge.grappa.debugger.mainwindow;

import com.github.fge.grappa.debugger.alert.AlertFactory;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class MainWindowDisplay
{
    private MainWindowPresenter presenter;

    @FXML
    BorderPane pane;

    @FXML
    Label label;

    public void init(final MainWindowPresenter presenter)
    {
        this.presenter = presenter;
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

    public void x(ActionEvent event)
    {
        new AlertFactory().unhandledError(new IOException());
    }
}
