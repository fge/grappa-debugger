package com.github.fge.grappa.debugger.csvtrace.tabs.stats;

import com.github.fge.grappa.debugger.common.BasePresenter;
import com.github.fge.grappa.debugger.common.GuiTaskRunner;
import com.github.fge.grappa.debugger.csvtrace.CsvTraceModel;
import com.github.fge.grappa.debugger.csvtrace.newmodel.ParseInfo;
import com.github.fge.grappa.debugger.mainwindow.MainWindowView;

public class StatsTabPresenter
    extends BasePresenter<StatsTabView>
{
    private final GuiTaskRunner taskRunner;
    private final MainWindowView mainView;
    private final CsvTraceModel model;

    public StatsTabPresenter(final GuiTaskRunner taskRunner,
        final MainWindowView mainView, final CsvTraceModel model)
    {
        this.taskRunner = taskRunner;
        this.mainView = mainView;
        this.model = model;
    }

    public void load()
    {
        final ParseInfo info = model.getParseInfo();

        view.showParseInfo(info);

        taskRunner.computeOrFail(
            () -> model.getNodeById(0).getNanos(),
            view::displayTotalParseTime,
            throwable -> mainView.showError("Load error",
                "Unable to load parse tree", throwable)
        );

        taskRunner.computeOrFail(
            model::getMatchersByType,
            view::displayMatchersByType,
            throwable -> mainView.showError("Load error",
                "Unable to load matcher statistics", throwable)
        );

        handleRefreshInvocationStatistics();
    }

    public void handleRefreshInvocationStatistics()
    {
        final boolean complete = model.isLoadComplete();

        taskRunner.computeOrFail(
            view::disableTableRefresh,
            model::getRuleInvocationStatistics,
            stats -> view.displayRuleInvocationStatistics(complete, stats),
            throwable -> mainView.showError("Load error",
                "Unable to load matcher statistics", throwable)
        );
    }
}
