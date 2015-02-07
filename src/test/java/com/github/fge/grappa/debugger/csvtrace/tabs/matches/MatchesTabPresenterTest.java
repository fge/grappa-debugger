package com.github.fge.grappa.debugger.csvtrace.tabs.matches;

import com.github.fge.grappa.debugger.common.GuiTaskRunner;
import com.github.fge.grappa.debugger.common.db.RuleInvocationStatistics;
import com.github.fge.grappa.debugger.csvtrace.CsvTraceModel;
import com.github.fge.grappa.debugger.mainwindow.MainWindowView;
import com.google.common.util.concurrent.MoreExecutors;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MatchesTabPresenterTest
{
    private final GuiTaskRunner taskRunner = new GuiTaskRunner(
        MoreExecutors.newDirectExecutorService(), Runnable::run
    );

    private MainWindowView mainView;
    private CsvTraceModel model;
    private MatchesTabView view;
    private MatchesTabPresenter presenter;

    @BeforeMethod
    public void init()
    {
        mainView = mock(MainWindowView.class);
        model = mock(CsvTraceModel.class);
        view = mock(MatchesTabView.class);
        presenter = spy(new MatchesTabPresenter(taskRunner, model, mainView));
        presenter.setView(view);
    }

    @Test
    public void loadTest()
    {
        doNothing().when(presenter).handleRefreshMatches();

        presenter.load();

        verify(presenter).handleRefreshMatches();
    }

    @Test
    public void handleRefreshInvocationStatisticsComplete()
    {
        @SuppressWarnings("unchecked")
        final List<RuleInvocationStatistics> stats = mock(List.class);

        //noinspection AutoBoxing
        when(model.isLoadComplete()).thenReturn(true);
        when(model.getMatches()).thenReturn(stats);

        presenter.handleRefreshMatches();

        verify(view).disableTabRefresh();
        verify(view).showMatchesLoadingComplete();
        verify(view).showMatches(same(stats));
    }

    @Test
    public void handleRefreshInvocationStatisticsIncomplete()
    {
        @SuppressWarnings("unchecked")
        final List<RuleInvocationStatistics> stats = mock(List.class);

        //noinspection AutoBoxing
        when(model.isLoadComplete()).thenReturn(false);
        when(model.getMatches()).thenReturn(stats);

        presenter.handleRefreshMatches();

        verify(view).disableTabRefresh();
        verify(view).showMatchesLoadingIncomplete();
        verify(view).showMatches(same(stats));
    }

    @Test
    public void handleRefreshInvocationStatisticsError()
    {
        final RuntimeException oops = new RuntimeException();

        when(model.getMatches()).thenThrow(oops);

        presenter.handleRefreshMatches();

        verify(view).disableTabRefresh();
        verify(view, never()).showMatchesLoadingIncomplete();
        verify(view, never()).showMatchesLoadingComplete();
        //noinspection unchecked
        verify(view, never()).showMatches(anyList());
        verify(presenter).handleRefreshMatchesError(same(oops));
    }
}
