package com.github.parboiled1.grappa.debugger;

import com.github.parboiled1.grappa.debugger.basewindow.BaseWindowPresenter;
import javafx.stage.Stage;

public interface BaseWindowFactory
{
    void createWindow();

    void close(BaseWindowPresenter presenter);

    Stage getStage(BaseWindowPresenter presenter);
}
