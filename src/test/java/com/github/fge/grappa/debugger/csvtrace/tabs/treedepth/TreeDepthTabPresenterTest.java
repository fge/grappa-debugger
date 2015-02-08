package com.github.fge.grappa.debugger.csvtrace.tabs.treedepth;

import com.github.fge.grappa.debugger.GrappaDebuggerException;
import com.github.fge.grappa.debugger.common.GuiTaskRunner;
import com.github.fge.grappa.debugger.csvtrace.CsvTraceModel;
import com.github.fge.grappa.debugger.mainwindow.MainWindowView;
import com.github.fge.grappa.debugger.model.ParseInfo;
import com.google.common.util.concurrent.MoreExecutors;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TreeDepthTabPresenterTest
{
    private final GuiTaskRunner taskRunner = new GuiTaskRunner(
        MoreExecutors.newDirectExecutorService(), Runnable::run
    );

    private TreeDepthTabView view;
    private ParseInfo info;
    private CsvTraceModel model;
    private MainWindowView mainView;
    private TreeDepthTabPresenter presenter;

    @BeforeMethod
    public void init()
    {
        view = mock(TreeDepthTabView.class);

        info = mock(ParseInfo.class);
        when(info.getNrLines()).thenReturn(Integer.MAX_VALUE);

        model = mock(CsvTraceModel.class);
        when(model.getParseInfo()).thenReturn(info);

        mainView = mock(MainWindowView.class);

        presenter = spy(new TreeDepthTabPresenter(taskRunner, mainView, model));
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
        presenter = spy(new TreeDepthTabPresenter(taskRunner, mainView, model));

        doNothing().when(presenter).doDisplayLines(anyInt(), anyInt());

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
        presenter = spy(new TreeDepthTabPresenter(taskRunner, mainView, model));

        doNothing().when(presenter).doDisplayLines(anyInt(), anyInt());

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
        presenter = spy(new TreeDepthTabPresenter(taskRunner, mainView, model));
        presenter.setView(view);

        presenter.startLine = realStartLine;
        presenter.displayedLines = displayedLines;

        presenter.adjustToolbar();

        verify(view, never()).enableNext();
    }

    @Test
    public void doDisplayLinesSuccessTest()
        throws GrappaDebuggerException
    {
        final int startLine = 10;
        final int wantedLines = 25;
        final List<Integer> depths = mock(List.class);

        when(model.getDepths(startLine, wantedLines)).thenReturn(depths);

        presenter.doDisplayLines(startLine, wantedLines);

        verify(view).disableToolbar();
        verify(model).getDepths(startLine, wantedLines);
        verify(view).displayDepths(eq(startLine), eq(wantedLines),
            same(depths));
        verify(presenter).adjustToolbar();
    }

    @Test
    public void doDisplayLinesFailureTest()
        throws GrappaDebuggerException
    {
        final int startLine = 10;
        final int wantedLines = 25;

        final Exception cause = new Exception();
        final GrappaDebuggerException exception
            = new GrappaDebuggerException(cause);

        when(model.getDepths(startLine, wantedLines)).thenThrow(exception);

        presenter.doDisplayLines(startLine, wantedLines);

        verify(view).disableToolbar();
        verify(model).getDepths(startLine, wantedLines);
        //noinspection unchecked
        verify(view, never()).displayDepths(anyInt(), anyInt(), anyList());
        verify(presenter).handleDisplayLinesError(same(exception));
        verify(presenter).adjustToolbar();
    }

    @Test
    public void loadSuccessTest()
        throws GrappaDebuggerException
    {
        doNothing().when(presenter).wakeUp();

        presenter.load();

        verify(model).waitForNodes();
        verify(presenter).wakeUp();
    }

    @Test
    public void loadFailureTest()
        throws GrappaDebuggerException
    {
        final Exception cause = new Exception();
        final GrappaDebuggerException exception
            = new GrappaDebuggerException(cause);

        doThrow(exception).when(model).waitForNodes();

        presenter.load();

        verify(presenter).handleDisplayLinesError(same(exception));
    }

    @Test
    public void wakeUpTest()
    {
        presenter.wakeUp();

        verify(view).wakeUp();
        verify(presenter).handleDisplayedLines(presenter.displayedLines);
    }
}
