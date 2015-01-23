package com.github.fge.grappa.debugger.mainwindow;

import com.github.fge.grappa.debugger.MainWindowFactory;
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

public class MainWindowPresenter
{
    private static final Map<String, ?> ZIPFS_ENV
        = Collections.singletonMap("readonly", "true");

    private final MainWindowFactory windowFactory;
    private final MainWindowView view;

    @VisibleForTesting
    LegacyTraceTabPresenter tabPresenter;

    public MainWindowPresenter(final MainWindowFactory windowFactory,
        final MainWindowView view)
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
        final LegacyTraceTabPresenter newTabPresenter;

        final Path path = view.chooseFile();

        if (path == null)
            return;

        MainWindowPresenter window = this;

        if (tabPresenter != null) {
            window = windowFactory.createWindow();
            if (window == null)
                return;
        }

        window.loadPresenter(path);
    }

    @VisibleForTesting
    void loadPresenter(final Path path)
    {
        view.setLabelText("Please wait...");

        final LegacyTraceTabPresenter tabPresenter;

        try {
            tabPresenter = loadFile(path);
        } catch (IOException e) {
            view.showError("Trace file error", "Unable to load trace file", e);
            return;
        }

        view.injectTab(tabPresenter);
        tabPresenter.loadTrace();
        view.setWindowTitle("Grappa debugger: " + path.toAbsolutePath());

        this.tabPresenter = tabPresenter;
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
