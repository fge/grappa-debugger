package com.github.parboiled1.grappa.debugger;

import com.github.parboiled1.grappa.debugger.basewindow.BaseWindowPresenter;
import javafx.stage.Stage;

public interface BaseWindowFactory
{
    void createWindow(Stage stage);

    default void createWindow()
    {
        createWindow(new Stage());
    }

    void close(BaseWindowPresenter presenter);
}
