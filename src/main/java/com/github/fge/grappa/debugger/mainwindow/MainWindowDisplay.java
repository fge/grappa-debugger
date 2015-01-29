package com.github.fge.grappa.debugger.mainwindow;

import com.github.fge.grappa.debugger.common.JavafxDisplay;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

public class MainWindowDisplay
    extends JavafxDisplay<MainWindowPresenter>
{
    @FXML
    BorderPane pane;

    @FXML
    Label label;

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
