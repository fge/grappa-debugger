package com.github.fge.grappa.debugger;

import com.github.fge.grappa.debugger.basewindow.BaseWindowPresenter;
import javafx.stage.Stage;

public interface BaseWindowFactory
{
    void createWindow();

    void close(BaseWindowPresenter presenter);

    Stage getStage(BaseWindowPresenter presenter);
}
