package com.github.fge.grappa.debugger.csvtrace;

import com.github.fge.grappa.debugger.JavafxViewTest;
import com.github.fge.grappa.debugger.common.GuiTaskRunner;
import com.github.fge.grappa.debugger.csvtrace.tabs.matches
    .JavafxMatchesTabView;
import com.github.fge.grappa.debugger.csvtrace.tabs.matches.MatchesTabDisplay;
import com.github.fge.grappa.debugger.csvtrace.tabs.matches.MatchesTabPresenter;
import com.github.fge.grappa.debugger.csvtrace.tabs.rules.JavafxRulesTabView;
import com.github.fge.grappa.debugger.csvtrace.tabs.rules.RulesTabDisplay;
import com.github.fge.grappa.debugger.csvtrace.tabs.rules.RulesTabPresenter;
import com.github.fge.grappa.debugger.csvtrace.tabs.tree.JavafxTreeTabView;
import com.github.fge.grappa.debugger.csvtrace.tabs.tree.TreeTabDisplay;
import com.github.fge.grappa.debugger.csvtrace.tabs.tree.TreeTabPresenter;
import com.github.fge.grappa.debugger.csvtrace.tabs.treedepth
    .JavafxTreeDepthTabView;
import com.github.fge.grappa.debugger.csvtrace.tabs.treedepth
    .TreeDepthTabDisplay;
import com.github.fge.grappa.debugger.csvtrace.tabs.treedepth
    .TreeDepthTabPresenter;
import com.github.fge.grappa.debugger.mainwindow.MainWindowView;
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

public class JavafxCsvTraceViewTest
    extends JavafxViewTest
{
    private final GuiTaskRunner taskRunner = new GuiTaskRunner(
        MoreExecutors.newDirectExecutorService(), Runnable::run);

    private MainWindowView parentView;
    private JavafxCsvTraceView view;
    private CsvTraceDisplay display;

    @BeforeMethod
    public void init()
        throws IOException
    {
        parentView = mock(MainWindowView.class);
        view = spy(new JavafxCsvTraceView(taskRunner, parentView));
        display = view.getDisplay();
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

    @Test
    public void showLoadCompleteTest()
    {
        view.showLoadComplete();

        assertThat(display.pane.getTop()).isNull();
    }


    @Test
    public void disableTabsRefreshTest()
    {
        display.refresh.setDisable(false);

        view.disableTabsRefresh();

        assertThat(display.refresh.isDisabled()).isTrue();
    }

    @Test
    public void enableTabsRefreshTest()
    {
        display.refresh.setDisable(true);

        view.enableTabsRefresh();

        assertThat(display.refresh.isDisabled()).isFalse();
    }
}
