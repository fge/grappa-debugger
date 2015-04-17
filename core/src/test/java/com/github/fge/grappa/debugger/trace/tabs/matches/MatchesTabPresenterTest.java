package com.github.fge.grappa.debugger.trace.tabs.matches;

import com.github.fge.grappa.debugger.ParseInfo;
import com.github.fge.grappa.debugger.TraceDb;
import com.github.fge.grappa.debugger.common.GuiTaskRunner;
import com.github.fge.grappa.debugger.main.MainWindowView;
import com.github.fge.grappa.debugger.model.TraceModel;
import com.github.fge.grappa.debugger.model.matches.MatchesData;
import com.google.common.util.concurrent.MoreExecutors;
import org.mockito.InOrder;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.concurrent.CountDownLatch;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MatchesTabPresenterTest
{
    private final GuiTaskRunner taskRunner = new GuiTaskRunner(
        MoreExecutors.newDirectExecutorService(), Runnable::run);

    private MainWindowView mainView;
    private TraceDb traceDb;
    private TraceModel model;
    private ParseInfo info;

    private MatchesTabPresenter presenter;

    private MatchesTabView view;

    @BeforeMethod
    public void init()
    {
        mainView = mock(MainWindowView.class);
        traceDb = mock(TraceDb.class);

        info = mock(ParseInfo.class);
        when(traceDb.getParseInfo()).thenReturn(info);

        model = mock(TraceModel.class);
        when(traceDb.getModel()).thenReturn(model);

        presenter = spy(new MatchesTabPresenter(taskRunner, mainView, traceDb));

        view = mock(MatchesTabView.class);

        presenter.setView(view);
    }

    @Test
    public void displayMatchesSuccessTest()
    {

        final MatchesData data = mock(MatchesData.class);

        when(model.getMatchesData()).thenReturn(data);

        presenter.displayMatches();

        verify(model).getMatchesData();
        verify(view).displayMatchesData(same(data));
        verify(presenter, never()).loadError(any(Throwable.class));
    }

    @Test
    public void displayMatchesFailureTest()
    {
        final RuntimeException exception = new RuntimeException();

        when(model.getMatchesData()).thenThrow(exception);

        doNothing().when(presenter).loadError(any(Throwable.class));
        presenter.displayMatches();

        verify(model).getMatchesData();
        verify(view, never()).displayMatchesData(any(MatchesData.class));
        verify(presenter).loadError(same(exception));
    }

    @Test
    public void loadTest()
    {
        doNothing().when(presenter).displayMatches();

        presenter.load();

        verify(presenter).displayMatches();
    }

    @Test
    public void refreshTest()
    {
        final CountDownLatch latch = presenter.refresh();

        verify(presenter).doRefresh(same(latch));

        assertThat(latch.getCount()).isEqualTo(0);
    }

    @Test
    public void doRefreshSuccessTest()
    {
        final MatchesData data = mock(MatchesData.class);

        when(model.getMatchesData()).thenReturn(data);

        final CountDownLatch latch = mock(CountDownLatch.class);
        presenter.doRefresh(latch);

        final InOrder inOrder = inOrder(model, view, presenter, latch);

        inOrder.verify(model).getMatchesData();
        inOrder.verify(latch).countDown();
        inOrder.verify(view).displayMatchesData(same(data));
        inOrder.verify(presenter, never()).loadError(any(Throwable.class));
    }

    @Test
    public void doRefreshFailureTest()
    {
        final RuntimeException exception = new RuntimeException();

        when(model.getMatchesData()).thenThrow(exception);

        final CountDownLatch latch = mock(CountDownLatch.class);
        presenter.doRefresh(latch);

        final InOrder inOrder = inOrder(model, view, presenter, latch);

        inOrder.verify(model).getMatchesData();
        inOrder.verify(latch).countDown();
        inOrder.verify(view, never())
            .displayMatchesData(any(MatchesData.class));
        inOrder.verify(presenter).loadError(same(exception));
    }
}
