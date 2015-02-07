package com.github.fge.grappa.debugger.csvtrace.tabs.linechart;

import com.github.fge.grappa.debugger.common.BasePresenter;
import com.github.fge.grappa.debugger.common.GuiTaskRunner;
import com.github.fge.grappa.debugger.csvtrace.CsvTraceModel;
import com.github.fge.grappa.debugger.mainwindow.MainWindowView;

public class LineChartTabPresenter
    extends BasePresenter<LineChartTabView>
{
    private final GuiTaskRunner taskRunner;
    private final CsvTraceModel model;
    private final MainWindowView mainView;

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
}
