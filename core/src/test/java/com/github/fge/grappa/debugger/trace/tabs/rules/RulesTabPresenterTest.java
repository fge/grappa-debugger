package com.github.fge.grappa.debugger.trace.tabs.rules;

import com.github.fge.grappa.debugger.TraceDb;
import com.github.fge.grappa.debugger.common.GuiTaskRunner;
import com.github.fge.grappa.debugger.main.MainWindowView;
import com.github.fge.grappa.debugger.model.TraceModel;
import com.github.fge.grappa.debugger.model.rules.PerClassStatistics;
import com.github.fge.grappa.matchers.MatcherType;
import com.google.common.util.concurrent.MoreExecutors;
import org.mockito.ArgumentCaptor;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RulesTabPresenterTest
{
    private final GuiTaskRunner taskRunner = new GuiTaskRunner(
        MoreExecutors.newDirectExecutorService(), Runnable::run);

    private MainWindowView mainView;
    private TraceDb traceDb;
    private TraceModel model;

    private RulesTabView view;

    private RulesTabPresenter presenter;

    @BeforeMethod
    public void init()
    {
        mainView = mock(MainWindowView.class);
        traceDb = mock(TraceDb.class);

        model = mock(TraceModel.class);

        when(traceDb.getModel()).thenReturn(model);

        view = mock(RulesTabView.class);

        presenter = spy(new RulesTabPresenter(taskRunner, mainView, traceDb));

        presenter.setView(view);
    }

    @Test
    public void loadTest()
    {
        doNothing().when(presenter).loadParseTime();
        doNothing().when(presenter).loadPieChart();
        doNothing().when(presenter).loadTable();

        presenter.load();

        verify(presenter).loadParseTime();
        verify(presenter).loadPieChart();
        verify(presenter).loadTable();
    }

    @SuppressWarnings("AutoBoxing")
    @Test
    public void loadParseTimeTest()
        throws InterruptedException
    {
        final long value = 42L;

        doReturn(value).when(presenter).getParseTime();

        presenter.loadParseTime();

        verify(presenter).getParseTime();
        verify(view).displayParseTime(value);
    }

    @Test
    public void loadPieCharTest()
    {
        @SuppressWarnings("unchecked")
        final Map<MatcherType, Integer> map = mock(Map.class);

        when(model.getMatchersByType()).thenReturn(map);

        presenter.loadPieChart();

        verify(model).getMatchersByType();
        verify(view).displayPieChart(same(map));
    }

    @Test
    public void loadTableTest()
    {
        @SuppressWarnings("unchecked")
        final List<PerClassStatistics> list = mock(List.class);

        when(model.getRulesByClass()).thenReturn(list);

        presenter.loadTable();

        verify(model).getRulesByClass();
        verify(view).displayTable(same(list));
    }

    @Test
    public void refreshTest()
    {
        doNothing().when(presenter).refreshPieChart(any(CountDownLatch.class));
        doNothing().when(presenter).refreshTable(any(CountDownLatch.class));

        final ArgumentCaptor<CountDownLatch> latch1
            = ArgumentCaptor.forClass(CountDownLatch.class);
        final ArgumentCaptor<CountDownLatch> latch2
            = ArgumentCaptor.forClass(CountDownLatch.class);


        final CountDownLatch latch = presenter.refresh();

        verify(presenter).refreshPieChart(latch1.capture());
        verify(presenter).refreshTable(latch2.capture());

        assertThat(latch1.getValue()).isSameAs(latch);
        assertThat(latch2.getValue()).isSameAs(latch);
    }

    @Test
    public void refreshPieChartTest()
    {
        @SuppressWarnings("unchecked")
        final Map<MatcherType, Integer> map = mock(Map.class);

        when(model.getMatchersByType()).thenReturn(map);

        final CountDownLatch latch = mock(CountDownLatch.class);

        presenter.refreshPieChart(latch);

        verify(model).getMatchersByType();
        verify(latch).countDown();
        verify(view).displayPieChart(same(map));
    }

    @Test
    public void refreshTableTest()
    {
        @SuppressWarnings("unchecked")
        final List<PerClassStatistics> list = mock(List.class);

        when(model.getRulesByClass()).thenReturn(list);

        final CountDownLatch latch = mock(CountDownLatch.class);

        presenter.refreshTable(latch);

        verify(model).getRulesByClass();
        verify(latch).countDown();
        verify(view).displayTable(same(list));
    }
}
