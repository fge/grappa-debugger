package com.github.parboiled1.grappa.debugger.basewindow;

import com.github.parboiled1.grappa.debugger.BaseWindowFactory;
import com.github.parboiled1.grappa.debugger.tracetab.TraceTabModel;
import com.github.parboiled1.grappa.debugger.tracetab.TraceTabPresenter;

public class BaseWindowPresenter
{
    private final BaseWindowFactory windowFactory;
    private final BaseWindowView view;

    public BaseWindowPresenter(final BaseWindowFactory windowFactory,
        final BaseWindowView view)
    {
        this.windowFactory = windowFactory;
        this.view = view;
    }

    public void handleCloseWindow()
    {
        windowFactory.close(this);
    }

    public void handleNewWindow()
    {
        windowFactory.createWindow();
    }

    public void handleLoadTab()
    {
        final TraceTabModel model = new TraceTabModel()
        {
        };
        final TraceTabPresenter presenter = new TraceTabPresenter(model);
        view.injectTab(presenter);
    }
}
