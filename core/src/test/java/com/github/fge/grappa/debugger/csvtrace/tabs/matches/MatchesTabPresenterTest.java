package com.github.fge.grappa.debugger.csvtrace.tabs.matches;

import com.github.fge.grappa.debugger.common.GuiTaskRunner;
import com.github.fge.grappa.debugger.csvtrace.CsvTraceModel;
import com.github.fge.grappa.debugger.mainwindow.MainWindowView;
import com.github.fge.grappa.debugger.model.tabs.matches.MatchesData;
import com.google.common.util.concurrent.MoreExecutors;
import org.mockito.InOrder;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.concurrent.CountDownLatch;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
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
        doNothing().when(presenter)
            .handleTabRefresh2(any(CountDownLatch.class));

        presenter.load();

        verify(presenter).handleTabRefresh2(any(CountDownLatch.class));
    }

    @Test
    public void handleTabRefresh2Test()
    {
        final MatchesData data = new MatchesData();
        final CountDownLatch latch = mock(CountDownLatch.class);

        when(model.getMatchesData()).thenReturn(data);

        presenter.handleTabRefresh2(latch);

        final InOrder inOrder = inOrder(model, latch, view, presenter);

        inOrder.verify(model).getMatchesData();
        inOrder.verify(latch).countDown();
        inOrder.verify(view).displayMatchesData(same(data));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void handleTabRefresh2FailureTest()
    {
        final RuntimeException exception = new RuntimeException();
        final CountDownLatch latch = mock(CountDownLatch.class);

        when(model.getMatchesData()).thenThrow(exception);

        presenter.handleTabRefresh2(latch);

        final InOrder inOrder = inOrder(model, latch, view, presenter);

        inOrder.verify(model).getMatchesData();
        inOrder.verify(latch).countDown();
        inOrder.verify(presenter).handleTabRefreshError(same(exception));
        inOrder.verifyNoMoreInteractions();
    }
}
