package com.github.fge.grappa.debugger.csvtrace.tabs.treedepth;

import com.github.fge.grappa.debugger.common.GuiTaskRunner;
import com.github.fge.grappa.debugger.csvtrace.CsvTraceModel;
import com.github.fge.grappa.debugger.javafx.TabPresenter;
import com.github.fge.grappa.debugger.mainwindow.MainWindowView;
import com.github.fge.grappa.debugger.model.ParseInfo;
import com.google.common.annotations.VisibleForTesting;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class TreeDepthTabPresenter
    extends TabPresenter<TreeDepthTabView>
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
        handleChartRefresh();
    }

    @Override
    public void refresh()
    {
        handleChartRefresh();
    }

    @Override
    protected void init()
    {
        final ParseInfo parseInfo = model.getParseInfo();
        view.setMaxLines(parseInfo.getNrLines());
    }

    public void handleChangeVisibleLines(final int visibleLines)
    {
        this.visibleLines = visibleLines;
        handleChangeStartLine(startLine);
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
        final int nrLines = model.getParseInfo().getNrLines();
        doRefreshChart(startLine, Math.min(nrLines, visibleLines));
    }

    @VisibleForTesting
    void doRefreshChart(final int startLine, final int visibleLines)
    {
        taskRunner.computeOrFail(
            view::disableToolbar,
            () -> model.getDepthMap(startLine, visibleLines),
            depthMap -> {
                view.displayChart(depthMap);
                updateToolbar(startLine, visibleLines);
            },
            this::handleRefreshChartError
        );
    }

    @VisibleForTesting
    void handleRefreshChartError(final Throwable throwable)
    {
        mainView.showError("Chart refresh error", "Unable to refresh chart",
            throwable);
    }

    public void updateToolbar(final int startLine, final int visibleLines)
    {
        view.updateStartLine(startLine);

        final int nrLines = model.getParseInfo().getNrLines();
        final boolean disableNext = startLine > nrLines - visibleLines;

        view.updateToolbar(startLine == 1, disableNext, model.isLoadComplete());
    }
}
