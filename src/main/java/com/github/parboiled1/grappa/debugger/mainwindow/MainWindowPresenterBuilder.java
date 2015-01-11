package com.github.parboiled1.grappa.debugger.mainwindow;

import com.github.parboiled1.grappa.debugger.alert.AlertFactory;
import javafx.stage.Stage;

import java.util.Objects;

public final class MainWindowPresenterBuilder
{
    Stage stage;
    MainWindowView view;
    MainWindowModel model;
    AlertFactory alertFactory;

    public MainWindowPresenterBuilder withStage(final Stage stage)
    {
        this.stage = Objects.requireNonNull(stage);
        return this;
    }

    public MainWindowPresenterBuilder withView(final MainWindowView view)
    {
        this.view = Objects.requireNonNull(view);
        return this;
    }

    public MainWindowPresenterBuilder withModel(final MainWindowModel model)
    {
        this.model = Objects.requireNonNull(model);
        return this;
    }

    public MainWindowPresenterBuilder withAlertFactory(
        final AlertFactory alertFactory)
    {
        this.alertFactory = Objects.requireNonNull(alertFactory);
        return this;
    }

    public MainWindowPresenter build()
    {
        return new MainWindowPresenter(this);
    }
}
