package com.github.fge.grappa.debugger;

import com.github.fge.grappa.debugger.basewindow.BaseWindowPresenter;

public interface BaseWindowFactory
{
    void createWindow();

    void close(BaseWindowPresenter presenter);
}
