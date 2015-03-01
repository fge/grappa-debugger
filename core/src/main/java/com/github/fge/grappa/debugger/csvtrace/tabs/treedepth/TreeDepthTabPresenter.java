package com.github.fge.grappa.debugger.csvtrace.tabs.treedepth;

import com.github.fge.grappa.debugger.GrappaDebuggerException;
import com.github.fge.grappa.debugger.common.GuiTaskRunner;
import com.github.fge.grappa.debugger.common.TabPresenter;
import com.github.fge.grappa.debugger.csvtrace.CsvTraceModel;
import com.github.fge.grappa.debugger.mainwindow.MainWindowView;
import com.github.fge.grappa.debugger.model.common.ParseInfo;
import com.google.common.annotations.VisibleForTesting;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

@ParametersAreNonnullByDefault
public class TreeDepthTabPresenter
    extends TabPresenter<TreeDepthTabView>
{
    private final MainWindowView mainView;
    private final CsvTraceModel model;

    @VisibleForTesting
    int startLine = 1;

    @VisibleForTesting
    int visibleLines = 25;

    public TreeDepthTabPresenter(final GuiTaskRunner taskRunner,
        final MainWindowView mainView, final CsvTraceModel model)
    {
        super(taskRunner);
        this.mainView = mainView;
        this.model = model;
    }

    @Override
    public void load()
    {
        refreshChart(new CountDownLatch(0));
    }

    @Override
    public CountDownLatch refresh()
    {
        final CountDownLatch latch = new CountDownLatch(1);
        refreshChart(latch);
        return latch;
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
        refreshChart(new CountDownLatch(0));
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

    @VisibleForTesting
    void refreshChart(final CountDownLatch latch)
    {
        final int nrLines = model.getParseInfo().getNrLines();
        doRefreshChart(startLine, Math.min(nrLines, visibleLines), latch);
    }

    @VisibleForTesting
    void doRefreshChart(final int startLine, final int visibleLines,
        final CountDownLatch latch)
    {
        taskRunner.computeOrFail(
            view::disableToolbar,
            () -> doGetDepthMap(startLine, visibleLines, latch),
            depthMap -> {
                view.displayChart(depthMap);
                updateToolbar(startLine, visibleLines);
            },
            this::handleRefreshChartError
        );
    }

    @VisibleForTesting
    Map<Integer, Integer> doGetDepthMap(final int startLine,
        final int visibleLines,
        final CountDownLatch latch)
        throws GrappaDebuggerException
    {
        try {
            return model.getDepthMap(startLine, visibleLines);
        } finally {
            latch.countDown();
        }
    }

    @VisibleForTesting
    void handleRefreshChartError(final Throwable throwable)
    {
        mainView.showError("Chart refresh error", "Unable to refresh chart",
            throwable);
    }

    @VisibleForTesting
    void updateToolbar(final int startLine, final int visibleLines)
    {
        view.updateStartLine(startLine);

        final int nrLines = model.getParseInfo().getNrLines();
        final boolean disableNext = startLine > nrLines - visibleLines;

        view.updateToolbar(startLine == 1, disableNext);
    }
}
