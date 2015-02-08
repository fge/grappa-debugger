package com.github.fge.grappa.debugger.csvtrace.tabs.treedepth;

import com.github.fge.grappa.debugger.csvtrace.CsvTraceModel;
import com.github.fge.grappa.debugger.model.ParseInfo;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TreeDepthTabPresenterTest
{
    private TreeDepthTabView view;
    private ParseInfo info;
    private CsvTraceModel model;
    private TreeDepthTabPresenter presenter;

    @BeforeMethod
    public void init()
    {
        view = mock(TreeDepthTabView.class);

        info = mock(ParseInfo.class);
        when(info.getNrLines()).thenReturn(Integer.MAX_VALUE);

        model = mock(CsvTraceModel.class);
        when(model.getParseInfo()).thenReturn(info);

        presenter = spy(new TreeDepthTabPresenter(model));
        presenter.setView(view);
    }

    @Test
    public void initialCallIsWithCorrectArguments()
    {
        final int nrLines = 42;

        presenter.handleDisplayedLines(nrLines);

        verify(presenter).doDisplayLines(1, nrLines);
    }

    @Test
    public void callIsIssuedWithCorrectStartLine()
    {
        final int startLine = 10;
        final int nrLines = 42;

        presenter.startLine = startLine;

        presenter.handleDisplayedLines(nrLines);

        verify(presenter).doDisplayLines(startLine, nrLines);
    }

    @Test
    public void numberOfLinesDisplayedIsChangedOnNewData()
    {
        final int oldLineNr = 20;
        final int newLineNr = 42;

        presenter.displayedLines = oldLineNr;

        presenter.handleDisplayedLines(newLineNr);

        assertThat(presenter.displayedLines).isEqualTo(newLineNr);
        verify(presenter).doDisplayLines(1, newLineNr);
    }

    @Test
    public void callIsAdjustedToActualNumberOfLines()
    {
        final int wantedLines = 42;
        final int availableLines = 25;

        when(info.getNrLines()).thenReturn(availableLines);
        presenter = spy(new TreeDepthTabPresenter(model));

        presenter.handleDisplayedLines(wantedLines);

        verify(presenter).doDisplayLines(1, availableLines);
    }

    @Test
    public void startLineIsAdjustedOnOverflow()
    {
        final int wantedLines = 25;
        final int availableLines = 42;
        final int startLine = 26;
        final int realStartLine = 18;

        when(info.getNrLines()).thenReturn(availableLines);
        presenter = spy(new TreeDepthTabPresenter(model));

        presenter.startLine = startLine;

        presenter.handleDisplayedLines(wantedLines);

        verify(presenter).doDisplayLines(realStartLine, wantedLines);
        assertThat(presenter.startLine).isEqualTo(realStartLine);
    }

    @Test
    public void startLineIsAdjustedOnUnderflow()
    {
        final int wantedLines = 25;
        final int startLine = -8;
        final int realStartLine = 1;

        presenter.startLine = startLine;

        presenter.handleDisplayedLines(wantedLines);

        verify(presenter).doDisplayLines(realStartLine, wantedLines);
        assertThat(presenter.startLine).isEqualTo(realStartLine);
    }

    @Test
    public void adjustToolbarLoadIncomplete()
    {
        when(model.isLoadComplete()).thenReturn(false);

        presenter.adjustToolbar();

        verify(view).enableTabRefresh();
    }

    @Test
    public void adjustToolbarLoadComplete()
    {
        when(model.isLoadComplete()).thenReturn(true);

        presenter.adjustToolbar();
        presenter.adjustToolbar();

        verify(view, times(1)).disableRefresh();
    }

    @Test
    public void adjustToolbarNotFirstLine()
    {
        presenter.startLine = 5;

        presenter.adjustToolbar();

        verify(view).enablePrevious();
    }

    @Test
    public void adjustToolbarFirstLine()
    {
        presenter.startLine = 1;

        presenter.adjustToolbar();

        verify(view, never()).enablePrevious();
    }

    @Test
    public void adjustToolbarNotLastLinesBatch()
    {
        presenter.adjustToolbar();

        verify(view).enableNext();
    }

    @Test
    public void adjustToolbarLastLinesBatch()
    {
        final int displayedLines = 25;
        final int nrLines = 42;
        final int realStartLine = 18;

        when(info.getNrLines()).thenReturn(nrLines);
        presenter = spy(new TreeDepthTabPresenter(model));
        presenter.setView(view);

        presenter.startLine = realStartLine;
        presenter.displayedLines = displayedLines;

        presenter.adjustToolbar();

        verify(view, never()).enableNext();
    }
}
