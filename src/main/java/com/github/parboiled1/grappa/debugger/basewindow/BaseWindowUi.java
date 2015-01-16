package com.github.parboiled1.grappa.debugger.basewindow;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;

public class BaseWindowUi
{
    private BaseWindowPresenter presenter;

    @FXML
    BorderPane pane;

    public void init(final BaseWindowPresenter presenter)
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
    void loadTabEvent(final ActionEvent event)
    {
        presenter.handleLoadTab();
    }
}
