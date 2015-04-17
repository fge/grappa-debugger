package com.github.fge.grappa.debugger.trace;

import com.github.fge.grappa.debugger.ParseInfo;
import com.github.fge.grappa.debugger.TraceDb;
import com.github.fge.grappa.debugger.TraceDbLoadStatus;
import com.github.fge.grappa.debugger.common.GuiTaskRunner;
import com.github.fge.grappa.debugger.main.MainWindowView;
import com.github.fge.grappa.debugger.model.TraceModel;
import com.github.fge.grappa.debugger.trace.tabs.TabPresenter;
import com.github.fge.grappa.debugger.trace.tabs.tree.TreeTabPresenter;
import com.google.common.util.concurrent.MoreExecutors;
import org.mockito.InOrder;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.concurrent.CountDownLatch;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

public class TracePresenterTest
{
    private final GuiTaskRunner taskRunner = new GuiTaskRunner(
        MoreExecutors.newDirectExecutorService(), Runnable::run);

    private MainWindowView mainView;
    private TraceDb traceDb;
    private TraceModel model;

    private TracePresenter presenter;
    private TraceView view;

    @BeforeMethod
    public void init()
    {
        mainView = mock(MainWindowView.class);
        traceDb = mock(TraceDb.class);
        model = mock(TraceModel.class);

        when(traceDb.getModel()).thenReturn(model);

        presenter = spy(new TracePresenter(mainView, taskRunner, traceDb));

        view = mock(TraceView.class);

        presenter.setView(view);
    }

    @Test
    public void loadTest()
        throws InterruptedException
    {
        doNothing().when(presenter).pollStatus();
        doNothing().when(presenter).loadTreeTab();

        presenter.load();

        verify(presenter).loadTreeTab();
        verify(presenter).pollStatus();
    }

    @SuppressWarnings("AutoBoxing")
    @Test
    public void pollStatusAlreadyLoadedTest()
        throws InterruptedException
    {
        final TraceDbLoadStatus status = mock(TraceDbLoadStatus.class);

        when(status.isReady()).thenReturn(true);

        when(traceDb.getLoadStatus()).thenReturn(status);

        presenter.pollStatus();

        verifyZeroInteractions(view);
    }

    // TODO: delegate that to GuiTaskRunner
    @SuppressWarnings("AutoBoxing")
    @Test
    public void pollStatusNotLoadedTest()
        throws InterruptedException
    {
        final TraceDbLoadStatus status = mock(TraceDbLoadStatus.class);
        final int loadedMatchers = 12;
        final int loadedNodes = 24;

        when(status.isReady())
            .thenReturn(false)
            .thenReturn(false)
            .thenReturn(true);
        when(status.getLoadedMatchers()).thenReturn(loadedMatchers);
        when(status.getLoadedNodes()).thenReturn(loadedNodes);

        when(traceDb.getLoadStatus()).thenReturn(status);

        final ParseInfo info = mock(ParseInfo.class);
        final int nrMatchers = 30;
        final int nrNodes = 42;

        when(info.getNrMatchers()).thenReturn(nrMatchers);
        when(info.getNrNodes()).thenReturn(nrNodes);

        when(traceDb.getParseInfo()).thenReturn(info);

        final int total = nrMatchers + nrNodes;
        final int current = loadedMatchers + loadedNodes;

        doNothing().when(presenter).pause();

        presenter.pollStatus();

        final InOrder inOrder = inOrder(presenter, view);

        inOrder.verify(view).showLoadToolbar();

        inOrder.verify(presenter).pause();
        inOrder.verify(view).reportStatus(total, current);

        inOrder.verify(view).showLoadComplete();
    }

    @Test
    public void loadTreeTabTest()
    {
        final TreeTabPresenter tabPresenter = mock(TreeTabPresenter.class);

        doReturn(tabPresenter).when(presenter).createTreeTabPresenter();

        final InOrder inOrder = inOrder(tabPresenter, view);

        presenter.loadTreeTab();

        inOrder.verify(view).loadTreeTab(tabPresenter);
        inOrder.verify(tabPresenter).load();

        assertThat(presenter.tabs).contains(tabPresenter);
    }

    @Test
    public void tabsRefreshEventTest()
    {
        doNothing().when(presenter).doRefreshTabs();
        doNothing().when(presenter).postTabsRefresh();

        presenter.handleTabsRefreshEvent();

        final InOrder inOrder = inOrder(presenter, view);

        inOrder.verify(view).disableTabRefresh();
        inOrder.verify(presenter).doRefreshTabs();
        inOrder.verify(presenter).postTabsRefresh();
    }

    @Test
    public void doRefreshTabsTest()
        throws InterruptedException
    {
        final CountDownLatch latch1 = mock(CountDownLatch.class);
        final TabPresenter<?> tab1 = mock(TabPresenter.class);
        when(tab1.refresh()).thenReturn(latch1);

        final CountDownLatch latch2 = mock(CountDownLatch.class);
        final TabPresenter<?> tab2 = mock(TabPresenter.class);
        when(tab2.refresh()).thenReturn(latch2);

        presenter.tabs.add(tab1);
        presenter.tabs.add(tab2);

        presenter.doRefreshTabs();

        verify(tab1, only()).refresh();
        verify(tab2, only()).refresh();
        verify(latch1, only()).await();
        verify(latch2, only()).await();
    }

    @SuppressWarnings("AutoBoxing")
    @Test
    public void postTabsRefreshNotReadyTest()
    {
        final TraceDbLoadStatus status = mock(TraceDbLoadStatus.class);

        // This is the default, but let's be explicit
        when(status.isReady()).thenReturn(false);

        when(traceDb.getLoadStatus()).thenReturn(status);

        presenter.postTabsRefresh();

        verify(view, only()).enableTabRefresh();
    }

    @SuppressWarnings("AutoBoxing")
    @Test
    public void postTabsRefreshReadyTest()
    {
        final TraceDbLoadStatus status = mock(TraceDbLoadStatus.class);

        when(status.isReady()).thenReturn(true);

        when(traceDb.getLoadStatus()).thenReturn(status);

        presenter.postTabsRefresh();

        verify(view, only()).hideLoadToolbar();
    }
}
