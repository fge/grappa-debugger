package com.github.fge.grappa.debugger.csvtrace.tabs.linechart;

import com.github.fge.grappa.debugger.common.BasePresenter;
import com.github.fge.grappa.debugger.common.GuiTaskRunner;
import com.github.fge.grappa.debugger.csvtrace.CsvTraceModel;
import com.github.fge.grappa.debugger.csvtrace.newmodel.LineMatcherStatus;
import com.github.fge.grappa.debugger.mainwindow.MainWindowView;
import com.google.common.annotations.VisibleForTesting;

import java.util.List;

public class LineChartTabPresenter
    extends BasePresenter<LineChartTabView>
{
    private final GuiTaskRunner taskRunner;
    private final CsvTraceModel model;
    private final MainWindowView mainView;

    @VisibleForTesting
    int startLine = 1;

    @VisibleForTesting
    int nrLines = 25;

    public LineChartTabPresenter(final GuiTaskRunner taskRunner,
        final CsvTraceModel model, final MainWindowView mainView)
    {
        this.taskRunner = taskRunner;
        this.model = model;
        this.mainView = mainView;
    }

    public void load()
    {
    }

    @VisibleForTesting
    void handleChangeLinesDisplayed(final int nrLines)
    {
        this.nrLines = nrLines;

        final boolean loadComplete = model.isLoadComplete();

        taskRunner.computeOrFail(
            view::disableTabRefresh,
            () -> model.getLineMatcherStatus(startLine, nrLines),
            list -> doChangeLinesDisplayed(list, loadComplete),
            this::handleLineMatcherLoadError
        );
    }

    @VisibleForTesting
    void doChangeLinesDisplayed(final List<LineMatcherStatus> list,
        final boolean loadComplete)
    {
        view.showLineMatcherStatus(list, startLine, nrLines);

        final Runnable runnable = loadComplete
            ? view::showLoadComplete
            : view::showLoadIncomplete;

        runnable.run();
    }

    @VisibleForTesting
    void handleLineMatcherLoadError(final Throwable throwable)
    {
        mainView.showError("Line match load error",
            "Unable to load per line matcher statistics", throwable);
    }
}
