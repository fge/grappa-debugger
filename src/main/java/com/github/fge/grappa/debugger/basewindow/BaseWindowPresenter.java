package com.github.fge.grappa.debugger.basewindow;

import com.github.fge.grappa.debugger.BaseWindowFactory;
import com.github.fge.grappa.debugger.legacy.tracetab.DefaultLegacyTraceTabModel;
import com.github.fge.grappa.debugger.legacy.tracetab.LegacyTraceTabModel;
import com.github.fge.grappa.debugger.legacy.tracetab.TraceTabPresenter;
import com.google.common.annotations.VisibleForTesting;

import java.io.IOException;
import java.nio.file.Path;

public class BaseWindowPresenter
{
    private final BaseWindowFactory windowFactory;
    private final BaseWindowView view;

    @VisibleForTesting
    TraceTabPresenter tabPresenter;

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
        if (tabPresenter != null) {
            final BaseWindowPresenter window = windowFactory.createWindow();
            if (window != null)
                window.handleLoadFile();
            return;
        }

        final Path path = view.chooseFile();

        if (path == null)
            return;

        final TraceTabPresenter newTabPresenter;

        view.setLabelText("Please wait...");

        try {
            newTabPresenter = loadFile(path);
        } catch (IOException e) {
            view.showError("Trace file error",
                "Unable to load trace file", e);
            return;
        }

        view.injectTab(newTabPresenter);
        newTabPresenter.loadTrace();
        view.setWindowTitle(
            "Grappa debugger: " + path.toAbsolutePath());

        tabPresenter = newTabPresenter;
    }

    @VisibleForTesting
    TraceTabPresenter loadFile(final Path path)
        throws IOException
    {
        final LegacyTraceTabModel model = new DefaultLegacyTraceTabModel(path.toRealPath());
        return new TraceTabPresenter(model);
    }
}
