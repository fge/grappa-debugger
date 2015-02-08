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
    private final int nrLines;

    @VisibleForTesting
    int startLine = 1;

    @VisibleForTesting
    int displayedLines = 25;

    public TreeDepthTabPresenter(final GuiTaskRunner taskRunner,
        final MainWindowView mainView, final CsvTraceModel model)
    {
        this.taskRunner = taskRunner;
        this.mainView = mainView;
        this.model = model;
        nrLines = model.getParseInfo().getNrLines();
    }

    @Override
    public void load()
    {
        taskRunner.runOrFail(
            model::waitForNodes,
            this::wakeUp,
            this::handleDisplayLinesError
        );
    }

    @VisibleForTesting
    void wakeUp()
    {
        view.wakeUp();
        handleDisplayedLines(displayedLines);
    }

    @Override
    protected void init()
    {
        view.setMaxLines(nrLines);
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
    void handleRequiredLine(final int lineNr)
    {
        startLine = lineNr;
        handleDisplayedLines(displayedLines);
    }

    @VisibleForTesting
    void doDisplayLines(final int startLine, final int nrLines)
    {
        taskRunner.computeOrFail(
            view::disableToolbar,
            () -> model.getDepths(startLine, nrLines),
            depths -> view.displayDepths(startLine, nrLines, depths),
            this::handleDisplayLinesError
        );

        taskRunner.executeFront(this::adjustToolbar);
    }

    @VisibleForTesting
    void handleDisplayLinesError(final Throwable throwable)
    {
        mainView.showError("Loading error", "Unable to load tree depths",
            throwable);
    }

    @VisibleForTesting
    void adjustToolbar()
    {
        if (startLine != 1)
            view.enablePrevious();

        if (startLine < nrLines - displayedLines + 1)
            view.enableNext();
    }
}
