package com.github.fge.grappa.debugger.csvtrace.tabs.matches;

import com.github.fge.grappa.debugger.common.BasePresenter;
import com.github.fge.grappa.debugger.common.GuiTaskRunner;
import com.github.fge.grappa.debugger.csvtrace.CsvTraceModel;
import com.github.fge.grappa.debugger.mainwindow.MainWindowView;
import com.google.common.annotations.VisibleForTesting;

public class MatchesTabPresenter
    extends BasePresenter<MatchesTabView>
{
    private final GuiTaskRunner taskRunner;
    private final CsvTraceModel model;
    private final MainWindowView mainView;

    public MatchesTabPresenter(final GuiTaskRunner taskRunner,
        final CsvTraceModel model, final MainWindowView mainView)
    {
        this.taskRunner = taskRunner;
        this.model = model;
        this.mainView = mainView;
    }

    public void load()
    {
        handleRefreshMatches();
    }

    @VisibleForTesting
    void handleRefreshMatches()
    {
        final boolean complete = model.isLoadComplete();

        taskRunner.computeOrFail(
            view::disableTabRefresh,
            model::getMatches,
            stats -> {
                if (complete)
                    view.showMatchesLoadingComplete();
                else
                    view.showMatchesLoadingIncomplete();
                view.showMatches(stats);
            }, this::handleRefreshMatchesError);
    }

    @VisibleForTesting
    void handleRefreshMatchesError(final Throwable throwable)
    {
        mainView.showError("Load error", "Unable to load matcher statistics",
            throwable);
    }
}
