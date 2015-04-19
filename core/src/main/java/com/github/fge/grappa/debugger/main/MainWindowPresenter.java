package com.github.fge.grappa.debugger.main;

import com.github.fge.grappa.debugger.MainWindowFactory;
import com.github.fge.grappa.debugger.TraceDb;
import com.github.fge.grappa.debugger.ZipTraceDbFactory;
import com.github.fge.grappa.debugger.common.BasePresenter;
import com.github.fge.grappa.debugger.common.GuiTaskRunner;
import com.github.fge.grappa.debugger.common.OnBackgroundThread;
import com.github.fge.grappa.debugger.common.OnUiThread;
import com.github.fge.grappa.debugger.trace.TracePresenter;
import com.google.common.annotations.VisibleForTesting;

import java.nio.file.Path;

public class MainWindowPresenter
    extends BasePresenter<MainWindowView>
{
    private final GuiTaskRunner taskRunner;
    private final MainWindowFactory windowFactory;

    private final ZipTraceDbFactory factory;

    @VisibleForTesting
    TracePresenter tracePresenter;

    public MainWindowPresenter(final MainWindowFactory windowFactory,
        final GuiTaskRunner taskRunner, final ZipTraceDbFactory factory)
    {
        this.windowFactory = windowFactory;
        this.taskRunner = taskRunner;
        this.factory = factory;
    }

    @Override
    public void load()
    {
        // TODO
    }

    public void handleCloseWindow()
    {
        if (tracePresenter != null)
            tracePresenter.close();
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

        window.loadTab(path);
    }

    @VisibleForTesting
    void loadTab(final Path path)
    {
        taskRunner.computeOrFail(
            () -> createPresenter(path),
            this::attachPresenter,
            this::handleLoadError
        );
    }

    @VisibleForTesting
    @OnBackgroundThread
    TracePresenter createPresenter(final Path path)
        throws Exception
    {
        final TraceDb traceDb = factory.create(path);
        return new TracePresenter(view, taskRunner, traceDb);
    }

    @VisibleForTesting
    @OnUiThread
    void attachPresenter(final TracePresenter presenter)
    {
        tracePresenter = presenter;
        view.attachPresenter(presenter);
        presenter.load();
        view.setWindowTitle(presenter.toString());
    }

    @VisibleForTesting
    @OnUiThread
    void handleLoadError(final Throwable throwable)
    {
        view.showError("Load failure", "Unable to load trace", throwable);
    }
}
