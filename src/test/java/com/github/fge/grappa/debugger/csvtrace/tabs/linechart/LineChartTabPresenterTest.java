package com.github.fge.grappa.debugger.csvtrace.tabs.linechart;

import com.github.fge.grappa.debugger.GrappaDebuggerException;
import com.github.fge.grappa.debugger.common.GuiTaskRunner;
import com.github.fge.grappa.debugger.csvtrace.CsvTraceModel;
import com.github.fge.grappa.debugger.csvtrace.newmodel.LineMatcherStatus;
import com.github.fge.grappa.debugger.csvtrace.newmodel.ParseInfo;
import com.github.fge.grappa.debugger.mainwindow.MainWindowView;
import com.google.common.util.concurrent.MoreExecutors;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class LineChartTabPresenterTest
{
    private final GuiTaskRunner taskRunner = new GuiTaskRunner(
        MoreExecutors.newDirectExecutorService(), Runnable::run
    );
    private final int totalLines = 100;

    private MainWindowView mainView;
    private CsvTraceModel model;
    private LineChartTabView view;
    private LineChartTabPresenter presenter;

    @BeforeMethod
    public void init()
    {
        mainView = mock(MainWindowView.class);
        model = mock(CsvTraceModel.class);

        final ParseInfo info = mock(ParseInfo.class);
        //noinspection AutoBoxing
        when(info.getNrLines()).thenReturn(totalLines);
        when(model.getParseInfo()).thenReturn(info);
        presenter = spy(new LineChartTabPresenter(taskRunner, model, mainView));

        view = mock(LineChartTabView.class);
        presenter.setView(view);
    }

    @SuppressWarnings({ "AutoBoxing", "unchecked" })
    @Test
    public void handleChangeLinesDisplayedSuccessTest()
        throws GrappaDebuggerException
    {
        final int startLine = 3;
        final int nrLines = 42;

        presenter.startLine = startLine;

        final List<LineMatcherStatus> list = mock(List.class);

        when(model.getLineMatcherStatus(startLine, nrLines)).thenReturn(list);
        when(model.isLoadComplete()).thenReturn(true);

        doNothing().when(presenter)
            .doChangeLinesDisplayed(anyList(), anyBoolean());

        presenter.handleChangeLinesDisplayed(nrLines);

        assertThat(presenter.nrLines).isEqualTo(nrLines);

        verify(model).getLineMatcherStatus(startLine, nrLines);
        verify(view).disableTabRefresh();
        verify(presenter).doChangeLinesDisplayed(same(list), eq(true));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void handleChangeLinesDisplayedErrorTest()
        throws GrappaDebuggerException
    {
        final int startLine = 3;
        final int nrLines = 42;

        presenter.startLine = startLine;

        final Exception cause = new Exception();
        final GrappaDebuggerException exception
            = new GrappaDebuggerException(cause);

        when(model.getLineMatcherStatus(startLine, nrLines))
            .thenThrow(exception);

        presenter.handleChangeLinesDisplayed(nrLines);

        assertThat(presenter.nrLines).isEqualTo(nrLines);

        verify(model).getLineMatcherStatus(startLine, nrLines);
        verify(view).disableTabRefresh();
        verify(presenter, never())
            .doChangeLinesDisplayed(anyList(), anyBoolean());
        verify(presenter).handleLineMatcherLoadError(same(exception));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void doChangeLinesDisplayedIncompleteTest()
    {
        final int startLine = 3;
        final int nrLines = 42;

        presenter.startLine = startLine;
        presenter.nrLines = nrLines;

        final List<LineMatcherStatus> list = mock(List.class);

        presenter.doChangeLinesDisplayed(list, false);

        verify(view).showLineMatcherStatus(same(list), eq(startLine), eq(
            nrLines));
        verify(view).showLoadIncomplete();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void doChangeLinesDisplayedCompleteTest()
    {
        final int startLine = 3;
        final int nrLines = 42;

        presenter.startLine = startLine;
        presenter.nrLines = nrLines;

        final List<LineMatcherStatus> list = mock(List.class);

        presenter.doChangeLinesDisplayed(list, true);

        verify(view).showLineMatcherStatus(same(list), eq(startLine),
            eq(nrLines));
        verify(view).showLoadComplete();
    }
}
