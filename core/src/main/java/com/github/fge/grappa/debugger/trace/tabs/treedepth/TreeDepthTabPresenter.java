package com.github.fge.grappa.debugger.trace.tabs.treedepth;

import com.github.fge.grappa.debugger.TraceDb;
import com.github.fge.grappa.debugger.common.GuiTaskRunner;
import com.github.fge.grappa.debugger.main.MainWindowView;
import com.github.fge.grappa.debugger.trace.tabs.TabPresenter;
import com.github.fge.grappa.internal.NonFinalForTesting;

import java.util.concurrent.CountDownLatch;

@NonFinalForTesting
public class TreeDepthTabPresenter
    extends TabPresenter<TreeDepthTabView>
{
    protected TreeDepthTabPresenter(final GuiTaskRunner taskRunner,
        final MainWindowView mainView, final TraceDb traceDb)
    {
        super(taskRunner, mainView, traceDb);
    }

    @Override
    public CountDownLatch refresh()
    {
        // TODO
        return null;
    }

    @Override
    public void load()
    {
        // TODO

    }
}
