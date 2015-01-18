package com.github.parboiled1.grappa.debugger.basewindow;

import com.github.parboiled1.grappa.debugger.BaseWindowFactory;
import com.github.parboiled1.grappa.debugger.tracetab.DefaultTraceTabModel;
import com.github.parboiled1.grappa.debugger.tracetab.TraceTabModel;
import com.github.parboiled1.grappa.debugger.tracetab.TraceTabPresenter;
import com.google.common.annotations.VisibleForTesting;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
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

    public void handleLoadFile()
    {
        final File file = view.chooseFile(windowFactory.getStage(this));

        if (file == null)
            return;

        final TraceTabPresenter presenter;

        try {
            presenter = loadFile(file.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        view.injectTab(presenter);
    }

    @VisibleForTesting
    TraceTabPresenter loadFile(final Path path)
        throws IOException
    {
        final TraceTabModel model = new DefaultTraceTabModel(path.toRealPath());
        return new TraceTabPresenter(model);
    }
}
