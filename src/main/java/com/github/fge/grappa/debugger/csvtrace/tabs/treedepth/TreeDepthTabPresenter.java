package com.github.fge.grappa.debugger.csvtrace.tabs.treedepth;

import com.github.fge.grappa.debugger.csvtrace.CsvTraceModel;
import com.github.fge.grappa.debugger.javafx.BasePresenter;
import com.google.common.annotations.VisibleForTesting;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class TreeDepthTabPresenter
    extends BasePresenter<TreeDepthTabView>
{
    private final CsvTraceModel model;
    private final int nrLines;

    @VisibleForTesting
    int startLine = 1;

    @VisibleForTesting
    int displayedLines = 25;

    private boolean refreshDisabled = false;

    public TreeDepthTabPresenter(final CsvTraceModel model)
    {
        this.model = model;
        nrLines = model.getParseInfo().getNrLines();
    }

    @VisibleForTesting
    void handleDisplayedLines(final int displayedLines)
    {
        this.displayedLines = displayedLines;

        int wantedLines = displayedLines;
        if (wantedLines > nrLines)
            wantedLines = nrLines;

        int actualStartLine = startLine;

        if (actualStartLine > nrLines - wantedLines + 1)
            actualStartLine = nrLines - wantedLines + 1;
        else if (actualStartLine < 1)
            actualStartLine = 1;

        startLine = actualStartLine;

        doDisplayLines(startLine, wantedLines);
    }

    @VisibleForTesting
    void handlePreviousLines()
    {
        startLine -= displayedLines;
        handleDisplayedLines(displayedLines);
    }

    @VisibleForTesting
    void handleNextLines()
    {
        // TODO: possible overflow
        startLine += displayedLines;
        handleDisplayedLines(displayedLines);
    }

    @VisibleForTesting
    void handleChartRefreshEvent()
    {
        handleDisplayedLines(displayedLines);
    }

    @VisibleForTesting
    void doDisplayLines(final int startLine, final int nrLines)
    {
        // TODO
    }

    @VisibleForTesting
    void adjustToolbar()
    {
        if (!refreshDisabled) {
            if (model.isLoadComplete()) {
                refreshDisabled = true;
                view.disableRefresh();
            } else {
                view.enableTabRefresh();
            }
        }

        if (startLine != 1)
            view.enablePrevious();

        if (startLine < nrLines - displayedLines + 1)
            view.enableNext();
    }
}
