package com.github.fge.grappa.debugger.csvtrace.tabs.matches;

import com.github.fge.grappa.debugger.common.GuiTaskRunner;
import com.github.fge.grappa.debugger.common.OnUiThread;
import com.github.fge.grappa.debugger.common.TabPresenter;
import com.github.fge.grappa.debugger.csvtrace.CsvTraceModel;
import com.github.fge.grappa.debugger.mainwindow.MainWindowView;
import com.google.common.annotations.VisibleForTesting;

import java.util.concurrent.CountDownLatch;

public class MatchesTabPresenter
    extends TabPresenter<MatchesTabView>
{
    public MatchesTabPresenter(final GuiTaskRunner taskRunner,
        final MainWindowView mainView, final CsvTraceModel model)
    {
        super(taskRunner, mainView, model);
    }

    @OnUiThread
    @Override
    public void load()
    {
        refresh();
    }

    @OnUiThread
    @Override
    public CountDownLatch refresh()
    {
        final CountDownLatch latch = new CountDownLatch(1);
        handleTabRefresh2(latch);
        return latch;
    }

    @OnUiThread
    @VisibleForTesting
    void handleTabRefreshError(final Throwable throwable)
    {
        mainView.showError("Load error", "Unable to load matcher statistics",
            throwable);
    }

    void handleTabRefresh2(final CountDownLatch latch)
    {
        taskRunner.computeOrFail(
            () -> {
                try {
                    return model.getMatchesData();
                } finally {
                    latch.countDown();
                }
            },
            view::displayMatchesData,
            this::handleTabRefreshError
        );
    }
}
