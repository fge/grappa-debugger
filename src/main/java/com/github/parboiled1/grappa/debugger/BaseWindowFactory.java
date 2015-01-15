package com.github.parboiled1.grappa.debugger;

import com.github.parboiled1.grappa.debugger.basewindow.BaseWindowPresenter;

public interface BaseWindowFactory
{
    void createWindow();

    void close(BaseWindowPresenter presenter);
}
