package com.github.fge.grappa.debugger.csvtrace.tabs.treedepth;

import com.github.fge.grappa.debugger.csvtrace.CsvTraceModel;
import com.github.fge.grappa.debugger.javafx.BasePresenter;
import com.google.common.annotations.VisibleForTesting;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class TreeDepthTabPresenter
    extends BasePresenter<TreeDepthTabView>
{
    private final int availableLines;

    @VisibleForTesting
    int startLine = 1;

    @VisibleForTesting
    int nrLines;

    public TreeDepthTabPresenter(final CsvTraceModel model)
    {
        availableLines = model.getParseInfo().getNrLines();
    }

    @VisibleForTesting
    void handleDisplayLines(final int nrLines)
    {
        this.nrLines = nrLines;

        int actualLines = nrLines;
        if (actualLines > availableLines)
            actualLines = availableLines;

        int actualStartLine = startLine;

        if (actualStartLine + actualLines > availableLines)
            actualStartLine = availableLines - actualLines + 1;
        else if (actualStartLine < 1)
            actualStartLine = 1;

        startLine = actualStartLine;

        doDisplayLines(startLine, actualLines);
    }

    @VisibleForTesting
    void handlePreviousLines()
    {
        startLine -= nrLines;
        handleDisplayLines(nrLines);
    }

    @VisibleForTesting
    void handleNextLines()
    {
        startLine += nrLines;
        handleDisplayLines(nrLines);
    }

    @VisibleForTesting
    void handleChartRefreshEvent()
    {
        // TODO
    }

    @VisibleForTesting
    void doDisplayLines(final int startLine, final int nrLines)
    {
        // TODO
    }
}
