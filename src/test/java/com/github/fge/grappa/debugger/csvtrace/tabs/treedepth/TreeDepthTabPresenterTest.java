package com.github.fge.grappa.debugger.csvtrace.tabs.treedepth;

import com.github.fge.grappa.debugger.GrappaDebuggerException;
import com.github.fge.grappa.debugger.common.GuiTaskRunner;
import com.github.fge.grappa.debugger.csvtrace.CsvTraceModel;
import com.github.fge.grappa.debugger.mainwindow.MainWindowView;
import com.github.fge.grappa.debugger.model.ParseInfo;
import com.google.common.util.concurrent.MoreExecutors;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
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

        model = mock(CsvTraceModel.class);
        when(model.getParseInfo()).thenReturn(info);

        mainView = mock(MainWindowView.class);

        presenter = spy(new TreeDepthTabPresenter(taskRunner, mainView, model));
        presenter.setView(view);
    }

    @Test
    public void initTest()
    {
        final int theAnswer = 42;
        final int depth = 100;

        when(info.getNrLines()).thenReturn(theAnswer);
        when(info.getTreeDepth()).thenReturn(depth);

        presenter.init();

        verify(view).setMaxLines(theAnswer);
        verify(view).setTreeDepth(depth);
    }

    @Test
    public void initialArgumentsTest()
    {
        assertThat(presenter.startLine).isEqualTo(1);
        assertThat(presenter.visibleLines).isEqualTo(25);
    }

    @Test
    public void handleChangeVisibleLinesTest()
    {
        final int theAnswer = 42;

        presenter.handleChangeVisibleLines(theAnswer);

        assertThat(presenter.visibleLines).isEqualTo(theAnswer);
        verify(presenter).refreshChart();
    }

    @DataProvider
    public Iterator<Object[]> startLineValues()
    {
        final List<Object[]> list = new ArrayList<>();

        list.add(Stream.of(42, 25, 1, 1).toArray());
        list.add(Stream.of(42, 25, 10, 10).toArray());
        list.add(Stream.of(42, 25, 20, 18).toArray());
        list.add(Stream.of(42, 25, 100, 18).toArray());
        list.add(Stream.of(10, 25, 1, 1).toArray());

        return list.iterator();
    }

    @Test(dataProvider = "startLineValues")
    public void handleChangeStartLineTest(final int nrLines,
        final int visibleLines, final int requested, final int expected)
    {
        when(info.getNrLines()).thenReturn(nrLines);

        presenter.visibleLines = visibleLines;

        presenter.handleChangeStartLine(requested);

        assertThat(presenter.startLine).isEqualTo(expected);

        verify(presenter).refreshChart();
    }

    @DataProvider
    public Iterator<Object[]> prevLineValues()
    {
        final List<Object[]> list = new ArrayList<>();

        list.add(Stream.of(25, 26, 1).toArray());
        list.add(Stream.of(25, 40, 15).toArray());
        list.add(Stream.of(50, 20, 1).toArray());

        return list.iterator();
    }

    @Test(dataProvider = "prevLineValues")
    public void handlePreviousLinesTest(final int visibleLines,
        final int currentStartLine, final int expected)
    {
        presenter.visibleLines = visibleLines;
        presenter.startLine = currentStartLine;

        presenter.handlePreviousLines();
        verify(presenter).handleChangeStartLine(expected);
    }

    @DataProvider
    public Iterator<Object[]> nextLineValues()
    {
        final List<Object[]> list = new ArrayList<>();

        list.add(Stream.of(25, 1, 26).toArray());
        list.add(Stream.of(42, 1, 43).toArray());
        list.add(Stream.of(42, 12, 54).toArray());
        list.add(Stream.of(20, Integer.MAX_VALUE, Integer.MAX_VALUE - 19)
            .toArray());
        list.add(Stream.of(25, Integer.MAX_VALUE - 24,
            Integer.MAX_VALUE - 24).toArray());

        return list.iterator();
    }

    @Test(dataProvider = "nextLineValues")
    public void handleNextLineTest(final int visibleLines,
        final int currentStartLine, final int expected)
    {
        presenter.visibleLines = visibleLines;
        presenter.startLine = currentStartLine;

        presenter.handleNextLines();
        verify(presenter).handleChangeStartLine(expected);
    }

    @Test
    public void handleChartRefreshTest()
    {
        presenter.handleChartRefresh();

        verify(presenter).refreshChart();
    }

    @DataProvider
    public Iterator<Object[]> chartValues()
    {
        final List<Object[]> list = new ArrayList<>();

        list.add(Stream.of(42, 25, 25).toArray());
        list.add(Stream.of(25, 25, 25).toArray());
        list.add(Stream.of(20, 25, 20).toArray());


        return list.iterator();
    }

    @Test(dataProvider = "chartValues")
    public void refreshChartTest(final int nrLines, final int visibleLines,
        final int expected)
    {
        when(info.getNrLines()).thenReturn(nrLines);

        presenter.visibleLines = visibleLines;

        presenter.refreshChart();

        verify(presenter).doRefreshChart(1, expected);
    }

    @Test
    public void doRefreshChartFailureTest()
        throws GrappaDebuggerException
    {
        final int startLine = 10;
        final int visibleLines = 30;

        final Exception cause = new Exception();
        final GrappaDebuggerException exception
            = new GrappaDebuggerException(cause);

        when(model.getDepthMap(anyInt(), anyInt()))
            .thenThrow(exception);

        presenter.doRefreshChart(startLine, visibleLines);

        verify(view).disableToolbar();
        verify(model).getDepthMap(startLine, visibleLines);
        verify(presenter).handleRefreshChartError(same(exception));
    }

    @Test
    public void doRefreshChartSuccessTest()
        throws GrappaDebuggerException
    {
        final int startLine = 10;
        final int visibleLines = 30;

        @SuppressWarnings("unchecked")
        final Map<Integer, Integer> depthMap = mock(Map.class);

        when(model.getDepthMap(anyInt(), anyInt()))
            .thenReturn(depthMap);

        presenter.doRefreshChart(startLine, visibleLines);

        verify(view).disableToolbar();
        verify(model).getDepthMap(startLine, visibleLines);
        verify(view).displayChart(same(depthMap));
        verify(presenter).updateToolbar(startLine, visibleLines);
    }

    @DataProvider
    public Iterator<Object[]> updateToolbarData()
    {
        final List<Object[]> list = new ArrayList<>();

        list.add(Stream.of(42, 1, 25, false, true, false, false).toArray());
        list.add(Stream.of(25, 1, 25, false, true, true, false).toArray());
        list.add(Stream.of(42, 2, 25, false, false, false, false).toArray());
        list.add(Stream.of(42, 2, 25, true, false, false, true).toArray());

        return list.iterator();
    }

    @Test(dataProvider = "updateToolbarData")
    public void updateToolbarTest(final int nrLines, final int startLine,
        final int visibleLines, final boolean loaded,
        final boolean disablePrev, final boolean disableNext,
        final boolean disableRefresh)
    {
        when(info.getNrLines()).thenReturn(nrLines);
        when(model.isLoadComplete()).thenReturn(loaded);

        presenter.startLine = startLine;
        presenter.visibleLines = visibleLines;

        presenter.updateToolbar(startLine, visibleLines);

        verify(view).updateStartLine(startLine);
        verify(view).updateToolbar(disablePrev, disableNext, disableRefresh);
    }
}
