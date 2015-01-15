package com.github.parboiled1.grappa.debugger.basewindow;

import com.github.parboiled1.grappa.debugger.BaseWindowFactory;

public final class BaseWindowPresenter
{
    private final BaseWindowFactory windowFactory;

    public BaseWindowPresenter(final BaseWindowFactory windowFactory)
    {
        this.windowFactory = windowFactory;
    }

    public void handleCloseWindow()
    {
        windowFactory.close(this);
    }

    public void handleNewWindow()
    {
        windowFactory.createWindow();
    }
}
