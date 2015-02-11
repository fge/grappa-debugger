package com.github.fge.grappa.debugger.csvtrace.tabs.matches;

import com.github.fge.grappa.debugger.common.GuiTaskRunner;
import com.github.fge.grappa.debugger.model.db.MatchStatistics;
import com.github.fge.grappa.debugger.csvtrace.CsvTraceModel;
import com.github.fge.grappa.debugger.mainwindow.MainWindowView;
import com.google.common.util.concurrent.MoreExecutors;
import org.mockito.InOrder;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
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
        doNothing().when(presenter).handleTabRefresh();

        presenter.load();

        verify(presenter).handleTabRefresh();
    }

    @Test
    public void refreshTest()
    {
        doNothing().when(presenter).handleTabRefresh();

        presenter.refresh();

        verify(presenter).handleTabRefresh();
    }

    @Test
    public void handleTabRefreshSuccessIncompleteTest()
    {
        //noinspection AutoBoxing
        when(model.isLoadComplete()).thenReturn(false);

        final MatchesTabPresenter.MatchersData data
            = mock(MatchesTabPresenter.MatchersData.class);

        doReturn(data).when(presenter).getMatchersData();

        doNothing().when(presenter)
            .updateTab(any(MatchesTabPresenter.MatchersData.class));

        presenter.handleTabRefresh();

        final InOrder inOrder = inOrder(view, presenter);

        inOrder.verify(view).disableTabRefresh();
        inOrder.verify(presenter).updateTab(same(data));
        inOrder.verify(view).showMatchesLoadingIncomplete();
    }

    @Test
    public void handleTabRefreshSuccessCompleteTest()
    {
        //noinspection AutoBoxing
        when(model.isLoadComplete()).thenReturn(true);

        final MatchesTabPresenter.MatchersData data
            = mock(MatchesTabPresenter.MatchersData.class);

        doReturn(data).when(presenter).getMatchersData();

        doNothing().when(presenter)
            .updateTab(any(MatchesTabPresenter.MatchersData.class));

        presenter.handleTabRefresh();

        final InOrder inOrder = inOrder(view, presenter);

        inOrder.verify(view).disableTabRefresh();
        inOrder.verify(presenter).updateTab(same(data));
        inOrder.verify(view).showMatchesLoadingComplete();
    }

    @Test
    public void handleTabRefreshFailureTest()
    {
        final RuntimeException exception = new RuntimeException();

        doThrow(exception).when(presenter).getMatchersData();

        presenter.handleTabRefresh();

        verify(view).disableTabRefresh();
        verify(presenter, never())
            .updateTab(any(MatchesTabPresenter.MatchersData.class));
        verify(presenter).handleTabRefreshError(same(exception));
        verifyNoMoreInteractions(view);
    }

    @SuppressWarnings({ "AutoBoxing", "UnnecessaryBoxing" })
    @Test
    public void updateTabTest()
    {
        final MatchesTabPresenter.MatchersData data
            = mock(MatchesTabPresenter.MatchersData.class);

        @SuppressWarnings("unchecked")
        final List<MatchStatistics> matches = mock(List.class);

        when(data.getMatches()).thenReturn(matches);

        when(data.getNonEmpty()).thenReturn(1);
        when(data.getEmpty()).thenReturn(2);
        when(data.getFailures()).thenReturn(3);
        when(data.getTotal()).thenReturn(6);

        final Integer topOne = Integer.valueOf(1);
        final Integer topFive = Integer.valueOf(5);
        final Integer topTen = Integer.valueOf(10);

        when(data.getTopOne()).thenReturn(topOne);
        when(data.getTopFive()).thenReturn(topFive);
        when(data.getTopTen()).thenReturn(topTen);

        presenter.updateTab(data);

        verify(view).showMatches(same(matches));
        verify(view).showMatchesStats(1, 2, 3);
        verify(view).showTopOne(same(topOne), eq(6));
        verify(view).showTopFive(same(topFive), eq(6));
        verify(view).showTopTen(same(topTen), eq(6));
        verifyNoMoreInteractions(view);
    }
}
