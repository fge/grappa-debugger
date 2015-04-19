package com.github.fge.grappa.debugger.javafx.trace;

import com.github.fge.grappa.debugger.JavafxViewTest;
import com.github.fge.grappa.debugger.common.GuiTaskRunner;
import com.github.fge.grappa.debugger.javafx.trace.tabs.matches
    .JavafxMatchesTabView;
import com.github.fge.grappa.debugger.javafx.trace.tabs.matches
    .MatchesTabDisplay;
import com.github.fge.grappa.debugger.javafx.trace.tabs.rules
    .JavafxRulesTabView;
import com.github.fge.grappa.debugger.javafx.trace.tabs.rules.RulesTabDisplay;
import com.github.fge.grappa.debugger.javafx.trace.tabs.tree.JavafxTreeTabView;
import com.github.fge.grappa.debugger.javafx.trace.tabs.tree.TreeTabDisplay;
import com.github.fge.grappa.debugger.javafx.trace.tabs.treedepth
    .JavafxTreeDepthTabView;
import com.github.fge.grappa.debugger.javafx.trace.tabs.treedepth
    .TreeDepthTabDisplay;
import com.github.fge.grappa.debugger.main.MainWindowView;
import com.github.fge.grappa.debugger.trace.tabs.matches.MatchesTabPresenter;
import com.github.fge.grappa.debugger.trace.tabs.rules.RulesTabPresenter;
import com.github.fge.grappa.debugger.trace.tabs.tree.TreeTabPresenter;
import com.github.fge.grappa.debugger.trace.tabs.treedepth
    .TreeDepthTabPresenter;
import com.google.common.util.concurrent.MoreExecutors;
import org.mockito.InOrder;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

public class JavafxTraceViewTest
    extends JavafxViewTest
{
    private final GuiTaskRunner taskRunner = new GuiTaskRunner(
        MoreExecutors.newDirectExecutorService(), Runnable::run);

    private MainWindowView parentView;

    private JavafxTraceView view;

    private TraceDisplay display;

    @BeforeMethod
    public void init()
        throws IOException
    {
        parentView = mock(MainWindowView.class);

        view = spy(new JavafxTraceView(taskRunner, parentView));
        display = view.getDisplay();
    }

    @Test
    public void showLoadToolbarTest()
    {
        view.showLoadToolbar();

        assertThat(display.pane.getTop()).isSameAs(display.toolbar);
    }

    @Test
    public void hideLoadToolbarTest()
    {
        view.hideLoadToolbar();

        assertThat(display.pane.getTop()).isNull();
    }

    @Test
    public void enableTabRefreshTest()
    {
        view.enableTabRefresh();

        assertThat(display.refresh.disabledProperty().getValue()).isFalse();
    }

    @Test
    public void disableTabRefreshTest()
    {
        view.disableTabRefresh();

        assertThat(display.refresh.disabledProperty().getValue()).isTrue();
    }

    @Test
    public void loadTreeTabSuccessTest()
        throws IOException
    {
        final JavafxTreeTabView tabView
            = spy(new JavafxTreeTabView(taskRunner));

        final TreeTabDisplay tabDisplay = mock(TreeTabDisplay.class);
        doReturn(tabDisplay).when(tabView).getDisplay();

        doReturn(tabView).when(view).getTreeTabView();

        final TreeTabPresenter tabPresenter = mock(TreeTabPresenter.class);

        view.loadTreeTab(tabPresenter);

        final InOrder inOrder = inOrder(tabDisplay, tabPresenter);

        inOrder.verify(tabDisplay).setPresenter(same(tabPresenter));
        inOrder.verify(tabPresenter).setView(same(tabView));
        inOrder.verifyNoMoreInteractions();

        assertThat(display.treeTab.contentProperty().getValue())
            .isSameAs(tabView.getNode());
    }

    @Test
    public void loadTreeTabFailureTest()
        throws IOException
    {
        final IOException exception = new IOException();

        doThrow(exception).when(view).getTreeTabView();

        final TreeTabPresenter tabPresenter = mock(TreeTabPresenter.class);

        view.loadTreeTab(tabPresenter);

        verify(parentView).showError(anyString(), anyString(), same(exception));

        verifyZeroInteractions(tabPresenter);
    }

    @Test
    public void loadRulesTabSuccessTest()
        throws IOException
    {
        final JavafxRulesTabView tabView = spy(new JavafxRulesTabView());

        final RulesTabDisplay tabDisplay = mock(RulesTabDisplay.class);
        doReturn(tabDisplay).when(tabView).getDisplay();

        doReturn(tabView).when(view).getRulesTabView();

        final RulesTabPresenter tabPresenter = mock(RulesTabPresenter.class);

        view.loadRulesTab(tabPresenter);

        final InOrder inOrder = inOrder(tabDisplay, tabPresenter);

        inOrder.verify(tabDisplay).setPresenter(same(tabPresenter));
        inOrder.verify(tabPresenter).setView(same(tabView));
        inOrder.verifyNoMoreInteractions();

        assertThat(display.rulesTab.contentProperty().getValue())
            .isSameAs(tabView.getNode());
    }

    @Test
    public void loadRulesTabFailureTest()
        throws IOException
    {
        final IOException exception = new IOException();

        doThrow(exception).when(view).getRulesTabView();

        final RulesTabPresenter tabPresenter = mock(RulesTabPresenter.class);

        view.loadRulesTab(tabPresenter);

        verify(parentView).showError(anyString(), anyString(), same(exception));

        verifyZeroInteractions(tabPresenter);
    }

    @Test
    public void loadMatchesTabSuccessTest()
        throws IOException
    {
        final JavafxMatchesTabView tabView = spy(new JavafxMatchesTabView());

        final MatchesTabDisplay tabDisplay = mock(MatchesTabDisplay.class);
        doReturn(tabDisplay).when(tabView).getDisplay();

        doReturn(tabView).when(view).getMatchesTabView();

        final MatchesTabPresenter tabPresenter = mock(MatchesTabPresenter.class);

        view.loadMatchesTab(tabPresenter);

        final InOrder inOrder = inOrder(tabDisplay, tabPresenter);

        inOrder.verify(tabDisplay).setPresenter(same(tabPresenter));
        inOrder.verify(tabPresenter).setView(same(tabView));
        inOrder.verifyNoMoreInteractions();

        assertThat(display.matchesTab.contentProperty().getValue())
            .isSameAs(tabView.getNode());
    }

    @Test
    public void loadMatchesTabFailureTest()
        throws IOException
    {
        final IOException exception = new IOException();

        doThrow(exception).when(view).getMatchesTabView();

        final MatchesTabPresenter tabPresenter = mock(MatchesTabPresenter.class);

        view.loadMatchesTab(tabPresenter);

        verify(parentView).showError(anyString(), anyString(), same(exception));

        verifyZeroInteractions(tabPresenter);
    }

    @Test
    public void loadTreeDepthTabSuccessTest()
        throws IOException
    {
        final JavafxTreeDepthTabView tabView
            = spy(new JavafxTreeDepthTabView());

        final TreeDepthTabDisplay tabDisplay = mock(TreeDepthTabDisplay.class);
        doReturn(tabDisplay).when(tabView).getDisplay();

        doReturn(tabView).when(view).getTreeDepthTabView();

        final TreeDepthTabPresenter tabPresenter
            = mock(TreeDepthTabPresenter.class);

        view.loadTreeDepthTab(tabPresenter);

        final InOrder inOrder = inOrder(tabDisplay, tabPresenter);

        inOrder.verify(tabDisplay).setPresenter(same(tabPresenter));
        inOrder.verify(tabPresenter).setView(same(tabView));
        inOrder.verifyNoMoreInteractions();

        assertThat(display.treeDepthTab.contentProperty().getValue())
            .isSameAs(tabView.getNode());
    }

    @Test
    public void loadTreeDepthTabFailureTest()
        throws IOException
    {
        final IOException exception = new IOException();

        doThrow(exception).when(view).getTreeDepthTabView();

        final TreeDepthTabPresenter tabPresenter = mock(TreeDepthTabPresenter.class);

        view.loadTreeDepthTab(tabPresenter);

        verify(parentView).showError(anyString(), anyString(), same(exception));

        verifyZeroInteractions(tabPresenter);
    }
}
