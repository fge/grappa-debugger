package com.github.fge.grappa.debugger.csvtrace;

import com.github.fge.grappa.debugger.GrappaDebuggerException;
import com.github.fge.grappa.debugger.common.GuiTaskRunner;
import com.github.fge.grappa.debugger.common.TabPresenter;
import com.github.fge.grappa.debugger.csvtrace.tabs.matches.MatchesTabPresenter;
import com.github.fge.grappa.debugger.csvtrace.tabs.rules.RulesTabPresenter;
import com.github.fge.grappa.debugger.csvtrace.tabs.tree.TreeTabPresenter;
import com.github.fge.grappa.debugger.csvtrace.tabs.treedepth
    .TreeDepthTabPresenter;
import com.github.fge.grappa.debugger.mainwindow.MainWindowView;
import com.google.common.util.concurrent.MoreExecutors;
import org.mockito.InOrder;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CsvTracePresenterTest
{
    private final GuiTaskRunner taskRunner = new GuiTaskRunner(
        MoreExecutors.newDirectExecutorService(), Runnable::run);

    private MainWindowView mainView;
    private CsvTraceModel model;
    private CsvTraceView view;
    private CsvTracePresenter presenter;

    @BeforeMethod
    public void init()
        throws IOException
    {
        model = mock(CsvTraceModel.class);

        mainView = mock(MainWindowView.class);
        presenter = spy(new CsvTracePresenter(mainView, taskRunner, model));

        view = mock(CsvTraceView.class);
        presenter.setView(view);
    }

    @Test
    public void loadTraceTest()
    {
        doNothing().when(presenter).loadTreeTab();
        doNothing().when(presenter).loadRulesTab();
        doNothing().when(presenter).loadMatchesTab();
        doNothing().when(presenter).loadTreeDepthTab();

        presenter.load();

        verify(presenter).loadTreeTab();
        verify(presenter).loadRulesTab();
        verify(presenter).loadMatchesTab();
        verify(presenter).loadTreeDepthTab();
    }

    @Test
    public void loadTreeTabTest()
    {
        final TreeTabPresenter tabPresenter = mock(TreeTabPresenter.class);

        doReturn(tabPresenter).when(presenter).createTreeTabPresenter();

        presenter.loadTreeTab();

        verify(view).loadTreeTab(tabPresenter);
        verify(tabPresenter).load();

        assertThat(presenter.tabs).contains(tabPresenter);
    }

    @Test
    public void loadStatsTabTest()
    {
        final RulesTabPresenter tabPresenter = mock(RulesTabPresenter.class);

        doReturn(tabPresenter).when(presenter).createRulesTabPresenter();

        presenter.loadRulesTab();

        verify(view).loadRulesTab(same(tabPresenter));
        verify(tabPresenter).load();

        assertThat(presenter.tabs).contains(tabPresenter);
    }

    @Test
    public void loadMatchesTabTest()
    {
        final MatchesTabPresenter tabPresenter
            = mock(MatchesTabPresenter.class);

        doReturn(tabPresenter).when(presenter).createMatchesTabPresenter();

        presenter.loadMatchesTab();

        verify(view).loadMatchesTab(same(tabPresenter));
        verify(tabPresenter).load();

        assertThat(presenter.tabs).contains(tabPresenter);
    }

    @Test
    public void loadTreeDepthTabTest()
    {
        final TreeDepthTabPresenter tabPresenter
            = mock(TreeDepthTabPresenter.class);

        doReturn(tabPresenter).when(presenter).createTreeDepthTabPresenter();

        presenter.loadTreeDepthTab();

        verify(view).loadTreeDepthTab(same(tabPresenter));
        verify(tabPresenter).load();

        assertThat(presenter.tabs).contains(tabPresenter);
    }

    @Test
    public void handleTabsRefreshEventTest()
    {
        doNothing().when(presenter).doRefreshTabs();
        doNothing().when(presenter).postTabsRefresh();

        presenter.handleTabsRefreshEvent();

        final InOrder inOrder = inOrder(presenter, view);

        inOrder.verify(view).disableTabsRefresh();
        inOrder.verify(presenter).doRefreshTabs();
        inOrder.verify(presenter).postTabsRefresh();
        inOrder.verifyNoMoreInteractions();
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

    @Test
    public void postTabsRefreshLoadIncompleteTest()
    {
        //noinspection AutoBoxing
        when(model.isLoadComplete()).thenReturn(false);

        presenter.postTabsRefresh();

        verify(view, only()).enableTabsRefresh();
    }

    @Test
    public void postTabsRefreshLoadCompleteTest()
    {
        //noinspection AutoBoxing
        when(model.isLoadComplete()).thenReturn(true);

        presenter.postTabsRefresh();

        verify(view, only()).showLoadComplete();
    }

    @Test
    public void disposeTest()
        throws GrappaDebuggerException
    {
        presenter.dispose();
        verify(model).dispose();
    }
}
