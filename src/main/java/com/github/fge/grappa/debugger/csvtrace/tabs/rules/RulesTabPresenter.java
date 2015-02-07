package com.github.fge.grappa.debugger.csvtrace.tabs.rules;

import com.github.fge.grappa.debugger.common.BasePresenter;
import com.github.fge.grappa.debugger.common.GuiTaskRunner;
import com.github.fge.grappa.debugger.csvtrace.CsvTraceModel;
import com.github.fge.grappa.debugger.csvtrace.newmodel.ParseInfo;
import com.github.fge.grappa.debugger.mainwindow.MainWindowView;
import com.google.common.annotations.VisibleForTesting;

public class RulesTabPresenter
    extends BasePresenter<RulesTabView>
{
    private final GuiTaskRunner taskRunner;
    private final MainWindowView mainView;
    private final CsvTraceModel model;

    public RulesTabPresenter(final GuiTaskRunner taskRunner,
        final MainWindowView mainView, final CsvTraceModel model)
    {
        this.taskRunner = taskRunner;
        this.mainView = mainView;
        this.model = model;
    }

    public void load()
    {
        loadParseInfo();
        loadTotalParseTime();
        loadMatchersByType();
        handleRefreshInvocationStatistics();
    }

    @VisibleForTesting
    void loadParseInfo()
    {
        final ParseInfo info = model.getParseInfo();
        view.displayParseInfo(info);
    }

    @VisibleForTesting
    void loadTotalParseTime()
    {
        taskRunner.computeOrFail(
            () -> model.getNodeById(0).getNanos(),
            view::displayTotalParseTime,
            this::handleLoadTotalParseTimeError
        );
    }

    @VisibleForTesting
    void handleLoadTotalParseTimeError(final Throwable throwable)
    {
       mainView.showError("Load error", "Unable to load parse tree", throwable);
    }

    @VisibleForTesting
    void loadMatchersByType()
    {
        taskRunner.computeOrFail(
            model::getMatchersByType,
            view::displayMatchersByType,
            this::handleLoadMatchersByTypeError
        );
    }

    @VisibleForTesting
    void handleLoadMatchersByTypeError(final Throwable throwable)
    {
       mainView.showError("Load error", "Unable to load matcher statistics",
           throwable);
    }

    public void handleRefreshInvocationStatistics()
    {
        final boolean complete = model.isLoadComplete();

        taskRunner.computeOrFail(
            view::disableTableRefresh,
            model::getMatches,
            stats -> {
                if (complete)
                    view.displayInvocationStatisticsComplete();
                else
                    view.displayInvocationStatisticsIncomplete();
                view.displayRuleInvocationStatistics(stats);
            },
            this::handleRefreshInvocationStatisticsError
        );
    }

    @VisibleForTesting
    void handleRefreshInvocationStatisticsError(final Throwable throwable)
    {
        mainView.showError("Load error", "Unable to load matcher statistics",
            throwable);
    }
}
