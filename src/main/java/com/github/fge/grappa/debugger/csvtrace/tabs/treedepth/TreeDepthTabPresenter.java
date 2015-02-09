package com.github.fge.grappa.debugger.csvtrace.tabs.treedepth;

import com.github.fge.grappa.debugger.common.GuiTaskRunner;
import com.github.fge.grappa.debugger.csvtrace.CsvTraceModel;
import com.github.fge.grappa.debugger.javafx.BasePresenter;
import com.github.fge.grappa.debugger.mainwindow.MainWindowView;
import com.google.common.annotations.VisibleForTesting;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class TreeDepthTabPresenter
    extends BasePresenter<TreeDepthTabView>
{
    private final GuiTaskRunner taskRunner;
    private final MainWindowView mainView;
    private final CsvTraceModel model;

    @VisibleForTesting
    int startLine = 1;

    @VisibleForTesting
    int visibleLines = 25;

    public TreeDepthTabPresenter(final GuiTaskRunner taskRunner,
        final MainWindowView mainView, final CsvTraceModel model)
    {
        this.taskRunner = taskRunner;
        this.mainView = mainView;
        this.model = model;
    }

    @Override
    public void load()
    {
    }

    @Override
    protected void init()
    {
        view.setMaxLines(model.getParseInfo().getNrLines());
    }

    public void handleChangeVisibleLines(final int visibleLines)
    {
        this.visibleLines = visibleLines;
        refreshChart();
    }

    public void handleChangeStartLine(final int startLine)
    {
        final int totalLines = model.getParseInfo().getNrLines();
        final int maxLastLine = Math.max(1, totalLines - visibleLines + 1);
        this.startLine = Math.min(startLine, maxLastLine);
        refreshChart();
    }

    public void handlePreviousLines()
    {
        final int nextStartLine = Math.max(startLine - visibleLines, 1);
        handleChangeStartLine(nextStartLine);
    }

    public void handleNextLines()
    {
        final int beforeOverflow = Integer.MAX_VALUE - visibleLines + 1;
        final int nextStartLine = startLine >= beforeOverflow
            ? beforeOverflow
            : startLine + visibleLines;
        handleChangeStartLine(nextStartLine);
    }

    public void handleChartRefresh()
    {
        refreshChart();
    }

    @VisibleForTesting
    void refreshChart()
    {
    }
}
