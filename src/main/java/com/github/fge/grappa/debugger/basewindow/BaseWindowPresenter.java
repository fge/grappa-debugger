package com.github.fge.grappa.debugger.basewindow;

import com.github.fge.grappa.debugger.BaseWindowFactory;
import com.github.fge.grappa.debugger.tracetab.DefaultTraceTabModel;
import com.github.fge.grappa.debugger.tracetab.TraceTabModel;
import com.github.fge.grappa.debugger.tracetab.TraceTabPresenter;
import com.google.common.annotations.VisibleForTesting;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

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
        presenter.loadTrace();
    }

    @VisibleForTesting
    TraceTabPresenter loadFile(final Path path)
        throws IOException
    {
        final TraceTabModel model = new DefaultTraceTabModel(path.toRealPath());
        return new TraceTabPresenter(model);
    }
}
