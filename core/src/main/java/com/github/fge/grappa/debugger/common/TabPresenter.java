package com.github.fge.grappa.debugger.common;

import com.github.fge.grappa.debugger.csvtrace.CsvTraceModel;
import com.github.fge.grappa.debugger.csvtrace.tabs.TabView;
import com.github.fge.grappa.debugger.mainwindow.MainWindowView;
import com.github.fge.grappa.debugger.model.common.ParseInfo;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.concurrent.CountDownLatch;

@ParametersAreNonnullByDefault
public abstract class TabPresenter<V extends TabView>
    extends BasePresenter<V>
{
    protected final GuiTaskRunner taskRunner;
    protected final MainWindowView mainView;
    protected final CsvTraceModel model;
    protected final ParseInfo info;

    protected TabPresenter(final GuiTaskRunner taskRunner,
        final MainWindowView mainView, final CsvTraceModel model)
    {
        this.taskRunner = taskRunner;
        this.mainView = mainView;
        this.model = model;
        info = model.getParseInfo();
    }

    @Override
    public void setView(final V view)
    {
        super.setView(view);
        view.displayInfo(info);
    }

    public abstract CountDownLatch refresh();
}
