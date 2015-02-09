package com.github.fge.grappa.debugger.csvtrace.tabs.treedepth;

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
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
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
        when(info.getNrLines()).thenReturn(theAnswer);

        presenter.init();

        verify(view).setMaxLines(theAnswer);
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
}
