package com.github.parboiled1.grappa.debugger.basewindow;

import com.github.parboiled1.grappa.debugger.BaseWindowFactory;
import com.github.parboiled1.grappa.debugger.tracetab.DefaultTraceTabModel;
import com.github.parboiled1.grappa.debugger.tracetab.TraceTabModel;
import com.github.parboiled1.grappa.debugger.tracetab.TraceTabPresenter;

import java.io.IOException;
import java.nio.file.Paths;

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
        final TraceTabModel model;
        try {
            model = new DefaultTraceTabModel(Paths.get("/tmp/trace.zip"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        final TraceTabPresenter presenter = new TraceTabPresenter(model);
        view.injectTab(presenter);
        presenter.loadTrace();
    }
}
