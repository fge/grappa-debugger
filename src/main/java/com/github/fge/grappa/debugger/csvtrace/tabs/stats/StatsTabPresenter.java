package com.github.fge.grappa.debugger.csvtrace.tabs.stats;

import com.github.fge.grappa.debugger.common.BasePresenter;
import com.github.fge.grappa.debugger.common.GuiTaskRunner;
import com.github.fge.grappa.debugger.csvtrace.CsvTraceModel;
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
}
