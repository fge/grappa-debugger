package com.github.fge.grappa.debugger.trace;

import com.github.fge.grappa.debugger.TraceDb;
import com.github.fge.grappa.debugger.common.GuiTaskRunner;
import com.github.fge.grappa.debugger.main.MainWindowView;
import com.github.fge.grappa.debugger.model.TraceModel;
import com.google.common.util.concurrent.MoreExecutors;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
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
}
