package com.github.parboiled1.grappa.debugger.basewindow;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class BaseWindowUi
{
    private BaseWindowPresenter presenter;

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
    public void closeWindowEvent(final ActionEvent event)
    {
        presenter.handleCloseWindow();
    }
}
