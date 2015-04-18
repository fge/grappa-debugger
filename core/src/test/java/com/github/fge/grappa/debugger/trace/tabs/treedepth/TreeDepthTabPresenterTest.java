package com.github.fge.grappa.debugger.trace.tabs.treedepth;

import com.github.fge.grappa.debugger.TraceDb;
import com.github.fge.grappa.debugger.common.GuiTaskRunner;
import com.github.fge.grappa.debugger.main.MainWindowView;
import com.github.fge.grappa.debugger.model.TraceModel;
import com.google.common.util.concurrent.MoreExecutors;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Map;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class TreeDepthTabPresenterTest
{
    private final GuiTaskRunner taskRunner = new GuiTaskRunner(
        MoreExecutors.newDirectExecutorService(), Runnable::run);

    private MainWindowView mainView;
    private TraceDb traceDb;
    private TraceModel model;

    private TreeDepthTabView view;

    private TreeDepthTabPresenter presenter;

    @BeforeMethod
    public void init()
    {
        mainView = mock(MainWindowView.class);
        traceDb = mock(TraceDb.class);

        model = mock(TraceModel.class);

        when(traceDb.getModel()).thenReturn(model);

        view = mock(TreeDepthTabView.class);

        presenter = spy(new TreeDepthTabPresenter(taskRunner, mainView,
            traceDb));
    }

    @Test
    public void loadTest()
    {
        @SuppressWarnings("unchecked")
        final Map<Integer, Integer> map = mock(Map.class);

        when(model.getDepthMap(anyInt(), anyInt())).thenReturn(map);
    }
}
