package com.github.fge.grappa.debugger.csvtrace.tabs.rules;

import com.github.fge.grappa.debugger.GrappaDebuggerException;
import com.github.fge.grappa.debugger.common.GuiTaskRunner;
import com.github.fge.grappa.debugger.csvtrace.CsvTraceModel;
import com.github.fge.grappa.debugger.mainwindow.MainWindowView;
import com.github.fge.grappa.debugger.model.tabs.rules.PerClassStatistics;
import com.github.fge.grappa.debugger.model.tabs.tree.ParseTreeNode;
import com.github.fge.grappa.matchers.MatcherType;
import com.google.common.util.concurrent.MoreExecutors;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RulesTabPresenterTest
{
    private final GuiTaskRunner taskRunner = new GuiTaskRunner(
        MoreExecutors.newDirectExecutorService(), Runnable::run
    );

    private MainWindowView mainView;
    private CsvTraceModel model;
    private RulesTabView view;
    private RulesTabPresenter presenter;

    @BeforeMethod
    public void init()
    {
        mainView = mock(MainWindowView.class);
        model = mock(CsvTraceModel.class);
        view = mock(RulesTabView.class);
        presenter = spy(new RulesTabPresenter(taskRunner, mainView, model));
        presenter.setView(view);
    }

    @Test
    public void loadTest()
    {
        doNothing().when(presenter).loadTotalParseTime();
        doReturn(mock(CountDownLatch.class)).when(presenter).refresh();

        presenter.load();

        verify(presenter).loadTotalParseTime();
        verify(presenter).refresh();
    }

    @Test
    public void refreshTest()
    {
        doNothing().when(presenter)
            .refreshMatchersByType(any(CountDownLatch.class));
        doNothing().when(presenter)
            .refreshRulesByClass(any(CountDownLatch.class));

        final CountDownLatch latch = presenter.refresh();

        verify(presenter).refreshMatchersByType(same(latch));
        verify(presenter).refreshRulesByClass(same(latch));
    }

    @Test
    public void refreshMatchersByTypeSuccessTest()
        throws GrappaDebuggerException
    {
        final CountDownLatch latch = mock(CountDownLatch.class);

        @SuppressWarnings("unchecked")
        final Map<MatcherType, Integer> map = mock(Map.class);

        doReturn(map).when(presenter).doGetMatchersByType(same(latch));

        presenter.refreshMatchersByType(latch);

        verify(presenter).doGetMatchersByType(same(latch));
        verify(view).displayMatchersByType(same(map));
        verify(presenter, never())
            .handleLoadMatchersByTypeError(any());
    }

    @Test
    public void refreshMatchersByTypeFailureTest()
        throws GrappaDebuggerException
    {
        final CountDownLatch latch = mock(CountDownLatch.class);

        final Exception cause = new Exception();
        final GrappaDebuggerException exception
            = new GrappaDebuggerException(cause);

        doThrow(exception).when(presenter).doGetMatchersByType(same(latch));

        presenter.refreshMatchersByType(latch);

        verify(presenter).doGetMatchersByType(same(latch));
        verify(view, never()).displayMatchersByType(anyMap());
        verify(presenter).handleLoadMatchersByTypeError(same(exception));
    }

    @Test
    public void doRefreshMatchersByTypeSuccessTest()
        throws GrappaDebuggerException
    {
        final CountDownLatch latch = mock(CountDownLatch.class);

        @SuppressWarnings("unchecked")
        final Map<MatcherType, Integer>  map = mock(Map.class);

        when(model.getMatchersByType()).thenReturn(map);

        final Map<MatcherType, Integer> actual
            = presenter.doGetMatchersByType(latch);

        assertThat(actual).isSameAs(map);
        verify(latch).countDown();
    }

    @Test
    public void doRefreshMatchersByTypeFailureTest()
        throws GrappaDebuggerException
    {
        final CountDownLatch latch = mock(CountDownLatch.class);

        final Exception cause = new Exception();
        final GrappaDebuggerException exception
            = new GrappaDebuggerException(cause);

        when(model.getMatchersByType()).thenThrow(exception);

        try {
            presenter.doGetMatchersByType(latch);
            failBecauseExceptionWasNotThrown(GrappaDebuggerException.class);
        } catch (GrappaDebuggerException e) {
            assertThat(e).isSameAs(exception);
        }

        verify(latch).countDown();
    }

    @Test
    public void refreshRulesByClassSuccessTest()
        throws GrappaDebuggerException
    {
        @SuppressWarnings("unchecked")
        final List<PerClassStatistics> list = mock(List.class);

        final CountDownLatch latch = mock(CountDownLatch.class);

        doReturn(list).when(presenter).doGetRulesByClass(same(latch));

        presenter.refreshRulesByClass(latch);

        verify(presenter).doGetRulesByClass(same(latch));
        verify(view).displayRules(same(list));
        verify(presenter, never()).handleRefreshRulesByClassError(any());
    }

    @Test
    public void refreshRulesByClassFailureTest()
        throws GrappaDebuggerException
    {
        final CountDownLatch latch = mock(CountDownLatch.class);

        final Exception cause = new Exception();
        final GrappaDebuggerException exception
            = new GrappaDebuggerException(cause);

        doThrow(exception).when(presenter).doGetRulesByClass(same(latch));

        presenter.refreshRulesByClass(latch);

        verify(presenter).doGetRulesByClass(same(latch));
        verify(view, never()).displayRules(anyList());
        verify(presenter).handleRefreshRulesByClassError(same(exception));
    }

    @Test
    public void doGetRulesByClassSuccessTest()
        throws GrappaDebuggerException
    {
        final CountDownLatch latch = mock(CountDownLatch.class);

        @SuppressWarnings("unchecked")
        final List<PerClassStatistics> list = mock(List.class);

        when(model.getRulesByClass()).thenReturn(list);

        final List<PerClassStatistics> actual
            = presenter.doGetRulesByClass(latch);

        assertThat(actual).isSameAs(list);

        verify(latch).countDown();
    }

    @Test
    public void doGetRulesByClassFailureTest()
        throws GrappaDebuggerException
    {
        final CountDownLatch latch = mock(CountDownLatch.class);

        final Exception cause = new Exception();
        final GrappaDebuggerException exception
            = new GrappaDebuggerException(cause);

        when(model.getRulesByClass()).thenThrow(exception);

        try {
            presenter.doGetRulesByClass(latch);
            failBecauseExceptionWasNotThrown(GrappaDebuggerException.class);
        } catch (GrappaDebuggerException e) {
            assertThat(e).isSameAs(exception);
        }

        verify(latch).countDown();
    }

    @Test
    public void loadTotalParseTimeOkTest()
        throws GrappaDebuggerException
    {
        final ParseTreeNode node = mock(ParseTreeNode.class);

        final long theAnswer = 42L;
        //noinspection AutoBoxing
        when(node.getNanos()).thenReturn(theAnswer);

        when(model.getNodeById(0)).thenReturn(node);

        presenter.loadTotalParseTime();

        verify(model).getNodeById(0);
        verify(view).displayTotalParseTime(theAnswer);
    }

    @Test
    public void loadTotalParseTimeFailTest()
        throws GrappaDebuggerException
    {
        final Exception cause = new Exception();
        final GrappaDebuggerException exception
            = new GrappaDebuggerException(cause);

        when(model.getNodeById(0)).thenThrow(exception);

        presenter.loadTotalParseTime();

        verify(model).getNodeById(0);
        verify(view, never()).displayTotalParseTime(anyLong());
        verify(presenter).handleLoadTotalParseTimeError(same(exception));
    }
}
