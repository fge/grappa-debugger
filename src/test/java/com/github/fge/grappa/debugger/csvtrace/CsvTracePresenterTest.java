package com.github.fge.grappa.debugger.csvtrace;

import com.github.fge.grappa.debugger.GrappaDebuggerException;
import com.github.fge.grappa.debugger.common.GuiTaskRunner;
import com.github.fge.grappa.debugger.csvtrace.tabs.matches.MatchesTabPresenter;
import com.github.fge.grappa.debugger.csvtrace.tabs.stats.StatsTabPresenter;
import com.github.fge.grappa.debugger.csvtrace.tabs.tree.TreeTabPresenter;
import com.github.fge.grappa.debugger.mainwindow.MainWindowView;
import com.google.common.util.concurrent.MoreExecutors;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

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
        doNothing().when(presenter).loadStatsTab();
        doNothing().when(presenter).loadMatchesTab();

        presenter.loadTrace();

        verify(presenter).loadTreeTab();
        verify(presenter).loadStatsTab();
        verify(presenter).loadMatchesTab();
    }

    @Test
    public void loadTreeTabTest()
    {
        final TreeTabPresenter tabPresenter = mock(TreeTabPresenter.class);

        doReturn(tabPresenter).when(presenter).createTreeTabPresenter();

        presenter.loadTreeTab();

        verify(view).loadTreeTab(tabPresenter);
        verify(tabPresenter).load();
    }

    @Test
    public void loadStatsTabTest()
    {
        final StatsTabPresenter tabPresenter = mock(StatsTabPresenter.class);

        doReturn(tabPresenter).when(presenter).createStatsTabPresenter();

        presenter.loadStatsTab();

        verify(view).loadStatsTab(tabPresenter);
        verify(tabPresenter).load();
    }

    @Test
    public void loadMatchesTabTest()
    {
        final MatchesTabPresenter tabPresenter
            = mock(MatchesTabPresenter.class);

        doReturn(tabPresenter).when(presenter).createMatchesTabPresenter();

        presenter.loadMatchesTab();

        verify(view).loadMatchesTab(tabPresenter);
        verify(tabPresenter).load();
    }

    @Test
    public void disposeTest()
        throws GrappaDebuggerException
    {
        presenter.dispose();
        verify(model).dispose();
    }
}
