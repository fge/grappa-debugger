package com.github.fge.grappa.debugger.common;

import com.github.fge.grappa.debugger.csvtrace.CsvTraceModel;
import com.github.fge.grappa.debugger.mainwindow.MainWindowView;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.concurrent.CountDownLatch;

@ParametersAreNonnullByDefault
public abstract class TabPresenter<V>
    extends BasePresenter<V>
{
    protected final GuiTaskRunner taskRunner;
    protected final MainWindowView mainView;
    protected final CsvTraceModel model;

    protected TabPresenter(final GuiTaskRunner taskRunner,
        final MainWindowView mainView, final CsvTraceModel model)
    {
        this.taskRunner = taskRunner;
        this.mainView = mainView;
        this.model = model;
    }

    public abstract CountDownLatch refresh();
}
