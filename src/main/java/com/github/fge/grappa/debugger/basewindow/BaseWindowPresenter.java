package com.github.fge.grappa.debugger.basewindow;

import com.github.fge.grappa.debugger.BaseWindowFactory;
import com.github.fge.grappa.debugger.legacy.tracetab
    .DefaultLegacyTraceTabModel;
import com.github.fge.grappa.debugger.legacy.tracetab.LegacyTraceTabModel;
import com.github.fge.grappa.debugger.legacy.tracetab.LegacyTraceTabPresenter;
import com.google.common.annotations.VisibleForTesting;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;

public class BaseWindowPresenter
{
    private static final Map<String, ?> ZIPFS_ENV
        = Collections.singletonMap("readonly", "true");

    private final BaseWindowFactory windowFactory;
    private final BaseWindowView view;

    @VisibleForTesting
    LegacyTraceTabPresenter tabPresenter;

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

        final LegacyTraceTabPresenter newTabPresenter;

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
    LegacyTraceTabPresenter loadFile(final Path path)
        throws IOException
    {
        final URI uri = URI.create("jar:" + path.toUri());

        try (
            final FileSystem zipfs = FileSystems.newFileSystem(uri, ZIPFS_ENV);
        ) {
            final LegacyTraceTabModel model
                = new DefaultLegacyTraceTabModel(zipfs);
            return new LegacyTraceTabPresenter(model);
        }
    }
}
