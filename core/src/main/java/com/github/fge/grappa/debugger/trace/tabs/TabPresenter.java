package com.github.fge.grappa.debugger.trace.tabs;

import com.github.fge.grappa.debugger.ParseInfo;
import com.github.fge.grappa.debugger.TraceDb;
import com.github.fge.grappa.debugger.common.BasePresenter;
import com.github.fge.grappa.debugger.common.GuiTaskRunner;
import com.github.fge.grappa.debugger.main.MainWindowView;
import com.google.common.annotations.VisibleForTesting;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.concurrent.CountDownLatch;

@ParametersAreNonnullByDefault
public abstract class TabPresenter<V extends TabView>
    extends BasePresenter<V>
{
    protected final GuiTaskRunner taskRunner;
    protected final MainWindowView mainView;
    protected final TraceDb traceDb;
    protected final ParseInfo info;

    protected TabPresenter(final GuiTaskRunner taskRunner,
        final MainWindowView mainView, final TraceDb traceDb)
    {
        this.taskRunner = taskRunner;
        this.mainView = mainView;
        this.traceDb = traceDb;
        info = traceDb.getParseInfo();
    }

    @Override
    public void setView(final V view)
    {
        super.setView(view);
        view.displayInfo(info);
    }

    public abstract CountDownLatch refresh();

    @VisibleForTesting
    public void showError(final String title, final String message,
        final Throwable throwable)
    {
        mainView.showError(title, message, throwable);
    }
}
