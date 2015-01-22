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
    private final BaseWindowGuiController guiController;

    @VisibleForTesting
    TraceTabPresenter tabPresenter;

    public BaseWindowPresenter(final BaseWindowFactory windowFactory,
        final BaseWindowGuiController guiController)
    {
        this.windowFactory = windowFactory;
        this.guiController = guiController;
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

        final File file = guiController.chooseFile();

        if (file == null)
            return;

        final TraceTabPresenter newTabPresenter;
        final Path path = file.toPath();

        guiController.setLabelText("Please wait...");

        try {
            newTabPresenter = loadFile(path);
        } catch (IOException e) {
            guiController.showError("Trace file error",
                "Unable to load trace file", e);
            return;
        }

        guiController.injectTab(newTabPresenter);
        newTabPresenter.loadTrace();
        guiController.setWindowTitle("Grappa debugger: "
            + path.toAbsolutePath());

        tabPresenter = newTabPresenter;
    }

    @VisibleForTesting
    TraceTabPresenter loadFile(final Path path)
        throws IOException
    {
        final TraceTabModel model = new DefaultTraceTabModel(path.toRealPath());
        return new TraceTabPresenter(model);
    }
}
