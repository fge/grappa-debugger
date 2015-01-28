package com.github.fge.grappa.debugger.mainwindow;

import com.github.fge.grappa.debugger.MainWindowFactory;
import com.github.fge.grappa.debugger.tracetab.DefaultTraceTabModel;
import com.github.fge.grappa.debugger.tracetab.TraceTabModel;
import com.github.fge.grappa.debugger.tracetab.TraceTabPresenter;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import javafx.application.Platform;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class MainWindowPresenter
{
    private static final ThreadFactory THREAD_FACTORY
        = new ThreadFactoryBuilder().setNameFormat("grappa-debugger-main-%d")
            .setDaemon(true).build();
    private static final Map<String, ?> ZIPFS_ENV
        = Collections.singletonMap("readonly", "true");

    private final ExecutorService executor
        = Executors.newSingleThreadExecutor(THREAD_FACTORY);

    private final MainWindowFactory windowFactory;
    private final MainWindowView view;

    @VisibleForTesting
    TraceTabPresenter tabPresenter;

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

        final URI uri = URI.create("jar:" + path.toUri());

        executor.submit(() -> {
            try (
                final FileSystem zipfs
                    = FileSystems.newFileSystem(uri, ZIPFS_ENV);
            ) {
                final boolean exists
                    = Files.exists(zipfs.getPath("/info.json"));
                if (!exists)
                    throw new NoSuchFileException("/info.json");
                tabPresenter = loadPresenter(zipfs);
                Platform.runLater(() -> loadTab(path));
            } catch (IOException e) {
                Platform.runLater(() -> handleLoadFileError(e));
            }
        });
    }

    private void loadTab(final Path path)
    {
        view.injectTab(tabPresenter);
        tabPresenter.loadTrace();
        view.setWindowTitle("Grappa debugger: " + path.toAbsolutePath());
    }

    private TraceTabPresenter loadPresenter(final FileSystem zipfs)
        throws IOException
    {
        final TraceTabModel model = new DefaultTraceTabModel(zipfs);
        return new TraceTabPresenter(view, model);
    }

    private void handleLoadFileError(final IOException e)
    {
        view.showError("Trace file error", "Unable to load trace file", e);
        view.setLabelText("Please load a trace file (File -> Load file)");
    }
}
