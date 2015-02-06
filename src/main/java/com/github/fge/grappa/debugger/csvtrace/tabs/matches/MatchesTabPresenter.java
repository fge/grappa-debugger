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

    public void handleRefreshStatistics()
    {
        final boolean complete = model.isLoadComplete();

        taskRunner.computeOrFail(
            view::disableTabRefresh,
            model::getRuleInvocationStatistics,
            stats -> {
                if (complete)
                    view.displayInvocationStatisticsComplete();
                else
                    view.displayInvocationStatisticsIncomplete();
                view.displayRuleInvocationStatistics(stats);
            },
            this::handleRefreshStatisticsError
        );
    }

    @VisibleForTesting
    void handleRefreshStatisticsError(final Throwable throwable)
    {
        mainView.showError("Load error", "Unable to load matcher statistics",
            throwable);
    }
}
