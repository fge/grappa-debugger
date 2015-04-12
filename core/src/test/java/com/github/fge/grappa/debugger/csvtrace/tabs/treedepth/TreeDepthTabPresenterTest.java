package com.github.fge.grappa.debugger.csvtrace.tabs.treedepth;

import com.github.fge.grappa.debugger.GrappaDebuggerException;
import com.github.fge.grappa.debugger.common.GuiTaskRunner;
import com.github.fge.grappa.debugger.csvtrace.CsvTraceModel;
import com.github.fge.grappa.debugger.mainwindow.MainWindowView;
import com.github.fge.grappa.debugger.model.common.ParseInfo;
import com.google.common.util.concurrent.MoreExecutors;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings("InstanceVariableMayNotBeInitialized")
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
    public void loadTest()
    {
        doNothing().when(presenter).refreshChart(any(CountDownLatch.class));

        presenter.load();

        verify(presenter).refreshChart(any(CountDownLatch.class));
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
        verify(presenter).handleChangeStartLine(presenter.startLine);
    }

    @SuppressWarnings("AutoBoxing")
    @DataProvider
    public Iterator<Object[]> startLineValues()
    {
        final Collection<Object[]> list = new ArrayList<>();

        list.add(Stream.of(42, 25, 1, 1).toArray());
        list.add(Stream.of(42, 25, 10, 10).toArray());
        list.add(Stream.of(42, 25, 20, 18).toArray());
        list.add(Stream.of(42, 25, 100, 18).toArray());
        list.add(Stream.of(10, 25, 1, 1).toArray());

        return list.iterator();
    }

    @SuppressWarnings("AutoBoxing")
    @Test(dataProvider = "startLineValues")
    public void handleChangeStartLineTest(final int nrLines,
        final int visibleLines, final int requested, final int expected)
    {
        when(info.getNrLines()).thenReturn(nrLines);

        presenter.visibleLines = visibleLines;

        presenter.handleChangeStartLine(requested);

        assertThat(presenter.startLine).isEqualTo(expected);

        verify(presenter).refreshChart(any(CountDownLatch.class));
    }

    @SuppressWarnings("AutoBoxing")
    @DataProvider
    public Iterator<Object[]> prevLineValues()
    {
        final Collection<Object[]> list = new ArrayList<>();

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

    @SuppressWarnings("AutoBoxing")
    @DataProvider
    public Iterator<Object[]> nextLineValues()
    {
        final Collection<Object[]> list = new ArrayList<>();

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

    @SuppressWarnings("AutoBoxing")
    @DataProvider
    public Iterator<Object[]> chartValues()
    {
        final Collection<Object[]> list = new ArrayList<>();

        list.add(Stream.of(42, 25, 25).toArray());
        list.add(Stream.of(25, 25, 25).toArray());
        list.add(Stream.of(20, 25, 20).toArray());


        return list.iterator();
    }

    @SuppressWarnings("AutoBoxing")
    @Test(dataProvider = "chartValues")
    public void refreshChartTest(final int nrLines, final int visibleLines,
        final int expected)
    {
        when(info.getNrLines()).thenReturn(nrLines);

        presenter.visibleLines = visibleLines;

        final CountDownLatch latch = mock(CountDownLatch.class);

        presenter.refreshChart(latch);

        verify(presenter).doRefreshChart(eq(1), eq(expected), same(latch));
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

        final CountDownLatch latch = mock(CountDownLatch.class);

        presenter.doRefreshChart(startLine, visibleLines, latch);

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

        final CountDownLatch latch = mock(CountDownLatch.class);

        presenter.doRefreshChart(startLine, visibleLines, latch);

        verify(view).disableToolbar();
        verify(model).getDepthMap(startLine, visibleLines);
        verify(view).displayChart(same(depthMap));
        verify(presenter).updateToolbar(startLine, visibleLines);
    }

    @SuppressWarnings("AutoBoxing")
    @DataProvider
    public Iterator<Object[]> updateToolbarData()
    {
        final Collection<Object[]> list = new ArrayList<>();

        list.add(Stream.of(42, 1, 25, true, false).toArray());
        list.add(Stream.of(25, 1, 25, true, true).toArray());
        list.add(Stream.of(42, 2, 25, false, false).toArray());
        list.add(Stream.of(42, 2, 25,false, false).toArray());
        list.add(Stream.of(151, 101, 50, false, false).toArray());

        return list.iterator();
    }

    @SuppressWarnings("AutoBoxing")
    @Test(dataProvider = "updateToolbarData")
    public void updateToolbarTest(final int nrLines, final int startLine,
        final int visibleLines, final boolean disablePrev,
        final boolean disableNext)
    {
        when(info.getNrLines()).thenReturn(nrLines);

        presenter.startLine = startLine;
        presenter.visibleLines = visibleLines;

        presenter.updateToolbar(startLine, visibleLines);

        verify(view).updateStartLine(startLine);
        verify(view).updateToolbar(disablePrev, disableNext);
    }
}
