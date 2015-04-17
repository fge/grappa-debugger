package com.github.fge.grappa.debugger.trace.tabs.matches;

import com.github.fge.grappa.debugger.TraceDb;
import com.github.fge.grappa.debugger.common.GuiTaskRunner;
import com.github.fge.grappa.debugger.common.OnBackgroundThread;
import com.github.fge.grappa.debugger.common.OnUiThread;
import com.github.fge.grappa.debugger.main.MainWindowView;
import com.github.fge.grappa.debugger.model.TraceModel;
import com.github.fge.grappa.debugger.model.matches.MatchesData;
import com.github.fge.grappa.debugger.trace.tabs.TabPresenter;
import com.github.fge.grappa.internal.NonFinalForTesting;
import com.github.fge.lambdas.Throwing;
import com.github.fge.lambdas.supplier.ThrowingSupplier;

import java.util.concurrent.CountDownLatch;

@NonFinalForTesting
public class MatchesTabPresenter
    extends TabPresenter<MatchesTabView>
{
    private final TraceModel model;

    public MatchesTabPresenter(final GuiTaskRunner taskRunner,
        final MainWindowView mainView, final TraceDb traceDb)
    {
        super(taskRunner, mainView, traceDb);
        model = traceDb.getModel();
    }

    @Override
    public CountDownLatch refresh()
    {
        final CountDownLatch latch = new CountDownLatch(1);
        doRefresh(latch);
        return latch;
    }

    @Override
    public void load()
    {
        displayMatches();
    }

    public void displayMatches()
    {
        taskRunner.computeOrFail(Throwing.supplier(model::getMatchesData),
            view::displayMatchesData, this::loadError);
    }

    @OnUiThread
    public void loadError(final Throwable throwable)
    {
        showError("Load error", "Cannot load matches data", throwable);
    }

    @OnBackgroundThread
    public void doRefresh(final CountDownLatch latch)
    {
        final ThrowingSupplier<MatchesData> supplier = () -> {
            try {
                return model.getMatchesData();
            } finally {
                latch.countDown();
            }
        };

        taskRunner.computeOrFail(supplier, view::displayMatchesData,
            this::loadError);
    }
}
