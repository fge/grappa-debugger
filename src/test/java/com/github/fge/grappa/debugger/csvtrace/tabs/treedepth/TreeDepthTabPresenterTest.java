package com.github.fge.grappa.debugger.csvtrace.tabs.treedepth;

import com.github.fge.grappa.debugger.csvtrace.CsvTraceModel;
import com.github.fge.grappa.debugger.model.ParseInfo;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
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

        presenter.handleDisplayLines(nrLines);

        verify(presenter).doDisplayLines(1, nrLines);
    }

    @Test
    public void callIsIssuedWithCorrectStartLine()
    {
        final int startLine = 10;
        final int nrLines = 42;

        presenter.startLine = startLine;

        presenter.handleDisplayLines(nrLines);

        verify(presenter).doDisplayLines(startLine, nrLines);
    }

    @Test
    public void numberOfLinesDisplayedIsChangedOnNewData()
    {
        final int oldLineNr = 20;
        final int newLineNr = 42;

        presenter.nrLines = oldLineNr;

        presenter.handleDisplayLines(newLineNr);

        assertThat(presenter.nrLines).isEqualTo(newLineNr);
        verify(presenter).doDisplayLines(1, newLineNr);
    }

    @Test
    public void callIsAdjustedToActualNumberOfLines()
    {
        final int wantedLines = 42;
        final int availableLines = 25;

        when(info.getNrLines()).thenReturn(availableLines);
        presenter = spy(new TreeDepthTabPresenter(model));

        presenter.handleDisplayLines(wantedLines);

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

        presenter.handleDisplayLines(wantedLines);

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

        presenter.handleDisplayLines(wantedLines);

        verify(presenter).doDisplayLines(realStartLine, wantedLines);
        assertThat(presenter.startLine).isEqualTo(realStartLine);

    }
}
