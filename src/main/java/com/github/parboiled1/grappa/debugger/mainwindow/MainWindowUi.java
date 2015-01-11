package com.github.parboiled1.grappa.debugger.mainwindow;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@ParametersAreNonnullByDefault
public class MainWindowUi
{
    private MainWindowPresenter presenter;
    private MainWindowView view;

    @FXML
    MenuItem loadInput;
    @FXML
    TextArea event;
    @FXML
    TextArea input;

    public void init(final MainWindowPresenter presenter,
        final MainWindowView view)
    {
        this.presenter = Objects.requireNonNull(presenter);
        this.view = Objects.requireNonNull(view);
    }

    @FXML
    public void loadInput(final ActionEvent ignored)
    {
        presenter.loadInput();
    }
}
