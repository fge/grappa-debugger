package com.github.fge.grappa.debugger.csvtrace.tabs.rules;

import com.github.fge.grappa.debugger.GrappaDebuggerException;
import com.github.fge.grappa.debugger.common.GuiTaskRunner;
import com.github.fge.grappa.debugger.csvtrace.CsvTraceModel;
import com.github.fge.grappa.debugger.mainwindow.MainWindowView;
import com.github.fge.grappa.debugger.model.ParseInfo;
import com.github.fge.grappa.debugger.model.ParseTreeNode;
import com.github.fge.grappa.debugger.model.db.PerClassStatistics;
import com.github.fge.grappa.matchers.MatcherType;
import com.google.common.util.concurrent.MoreExecutors;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.doNothing;
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
        doNothing().when(presenter).loadParseInfo();
        doNothing().when(presenter).loadTotalParseTime();
        doNothing().when(presenter).loadMatchersByType();
        doNothing().when(presenter).handleRefreshRules();

        presenter.load();

        verify(presenter).loadParseInfo();
        verify(presenter).loadTotalParseTime();
        verify(presenter).loadMatchersByType();
        verify(presenter).handleRefreshRules();
    }

    @Test
    public void loadParseInfoTest()
    {
        final ParseInfo info = mock(ParseInfo.class);
        when(model.getParseInfo()).thenReturn(info);

        presenter.loadParseInfo();

        verify(view).displayParseInfo(same(info));
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

    @Test
    public void loadMatchersByTypeOkTest()
        throws GrappaDebuggerException
    {
        @SuppressWarnings("unchecked")
        final Map<MatcherType, Integer> map = mock(Map.class);

        when(model.getMatchersByType()).thenReturn(map);

        presenter.loadMatchersByType();

        verify(model).getMatchersByType();
        verify(view).displayMatchersByType(same(map));
    }

    @Test
    public void loadMatchersByTypeFailTest()
        throws GrappaDebuggerException
    {
        final Exception cause = new Exception();
        final GrappaDebuggerException exception
            = new GrappaDebuggerException(cause);

        when(model.getMatchersByType()).thenThrow(exception);

        presenter.loadMatchersByType();

        verify(model).getMatchersByType();
        verify(presenter).handleLoadMatchersByTypeError(same(exception));
        //noinspection unchecked
        verify(view, never()).displayMatchersByType(anyMap());
    }

    @Test
    public void handleRefreshRulesSuccessTest()
        throws GrappaDebuggerException
    {
        @SuppressWarnings("unchecked")
        final List<PerClassStatistics> list = mock(List.class);

        when(model.getRulesByClass()).thenReturn(list);

        presenter.handleRefreshRules();

        verify(view).disableRefreshRules();
        verify(model).getRulesByClass();
        verify(presenter).doHandleRefreshRules(same(list));
    }

    @Test
    public void handleRefreshRulesFailureTest()
        throws GrappaDebuggerException
    {
        final Exception cause = new Exception();
        final GrappaDebuggerException exception
            = new GrappaDebuggerException(cause);

        when(model.getRulesByClass()).thenThrow(exception);

        presenter.handleRefreshRules();

        verify(view).disableRefreshRules();
        verify(model).getRulesByClass();
        //noinspection unchecked
        verify(presenter, never()).doHandleRefreshRules(anyList());
        verify(presenter).handleRefreshRulesError(same(exception));
    }

    @Test
    public void doHandleRefreshRulesCompleteTest()
    {
        @SuppressWarnings("unchecked")
        final List<PerClassStatistics> stats = mock(List.class);

        //noinspection AutoBoxing
        when(model.isLoadComplete()).thenReturn(true);

        presenter.doHandleRefreshRules(stats);

        verify(view).displayRules(same(stats));
        verify(view).hideRefreshRules();
    }

    @Test
    public void doHandleRefreshRulesIncompleteTest()
    {
        @SuppressWarnings("unchecked")
        final List<PerClassStatistics> stats = mock(List.class);

        //noinspection AutoBoxing
        when(model.isLoadComplete()).thenReturn(false);

        presenter.doHandleRefreshRules(stats);

        verify(view).displayRules(same(stats));
        verify(view).enableRefreshRules();
    }
}
