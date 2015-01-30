package com.github.fge.grappa.debugger.mainwindow;

import com.github.fge.grappa.debugger.MainWindowFactory;
import com.github.fge.grappa.debugger.common.BackgroundTaskRunner;
import com.github.fge.grappa.debugger.common.BasePresenter;
import com.github.fge.grappa.debugger.csvtrace.CsvTraceModel;
import com.github.fge.grappa.debugger.csvtrace.CsvTracePresenter;
import com.github.fge.grappa.debugger.csvtrace.DefaultCsvTraceModel;
import com.google.common.annotations.VisibleForTesting;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

@ParametersAreNonnullByDefault
public class MainWindowPresenter
    extends BasePresenter<MainWindowView>
{
    private static final Map<String, ?> ZIPFS_ENV
        = Collections.singletonMap("readonly", "true");

    private final BackgroundTaskRunner taskRunner;

    private final MainWindowFactory windowFactory;

    @VisibleForTesting
    CsvTracePresenter tracePresenter;

    public MainWindowPresenter(final MainWindowFactory windowFactory,
        final BackgroundTaskRunner taskRunner)
    {
        this.windowFactory = Objects.requireNonNull(windowFactory);
        this.taskRunner = Objects.requireNonNull(taskRunner);
    }

    public void handleCloseWindow()
    {
        if (tracePresenter != null)
            tracePresenter.dispose();
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

        if (tracePresenter != null) {
            window = windowFactory.createWindow();
            if (window == null)
                return;
        }

        try {
            tracePresenter = loadTrace(path);
        } catch (IOException e) {
            handleLoadFileError(e);
            return;
        }

        window.view.attachTrace(tracePresenter);
        tracePresenter.loadTrace();
    }

    @VisibleForTesting
    CsvTracePresenter loadTrace(final Path path)
        throws IOException
    {
        final URI uri = URI.create("jar:" + path.toUri());
        final FileSystem zipfs = FileSystems.newFileSystem(uri, ZIPFS_ENV);
        final CsvTraceModel model = new DefaultCsvTraceModel(zipfs);
        return new CsvTracePresenter(view, taskRunner, model);
    }

    private void handleLoadFileError(final Throwable throwable)
    {
        view.showError("Trace file error", "Unable to load trace file",
            throwable);
        view.setLabelText("Please load a trace file (File -> Load file)");
    }
}
