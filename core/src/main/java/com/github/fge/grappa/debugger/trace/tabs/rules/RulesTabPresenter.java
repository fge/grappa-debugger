package com.github.fge.grappa.debugger.trace.tabs.rules;

import com.github.fge.grappa.debugger.TraceDb;
import com.github.fge.grappa.debugger.TraceDbLoadStatus;
import com.github.fge.grappa.debugger.common.GuiTaskRunner;
import com.github.fge.grappa.debugger.common.OnBackgroundThread;
import com.github.fge.grappa.debugger.common.OnUiThread;
import com.github.fge.grappa.debugger.main.MainWindowView;
import com.github.fge.grappa.debugger.model.TraceModel;
import com.github.fge.grappa.debugger.model.rules.PerClassStatistics;
import com.github.fge.grappa.debugger.trace.tabs.TabPresenter;
import com.github.fge.grappa.matchers.MatcherType;
import com.google.common.annotations.VisibleForTesting;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class RulesTabPresenter
    extends TabPresenter<RulesTabView>
{
    private final TraceModel model;

    public RulesTabPresenter(final GuiTaskRunner taskRunner,
        final MainWindowView mainView, final TraceDb traceDb)
    {
        super(taskRunner, mainView, traceDb);
        model = traceDb.getModel();
    }

    @Override
    public CountDownLatch refresh()
    {
        final CountDownLatch latch = new CountDownLatch(2);

        refreshPieChart(latch);
        refreshTable(latch);

        return latch;
    }

    @Override
    public void load()
    {
        loadParseTime();
        loadPieChart();
        loadTable();
    }

    @VisibleForTesting
    void loadParseTime()
    {
        // TODO: define methods for {Int,Double,Long}Supplier etc
        taskRunner.computeOrFail(
            this::getParseTime,
            view::displayParseTime,
            this::loadError
        );
    }

    @VisibleForTesting
    void loadPieChart()
    {
        taskRunner.compute(model::getMatchersByType, view::displayPieChart);
    }

    @VisibleForTesting
    void loadTable()
    {
        taskRunner.compute(
            model::getRulesByClass,
            view::displayTable
        );
    }

    @VisibleForTesting
    @OnBackgroundThread
    long getParseTime()
        throws InterruptedException
    {
        final TraceDbLoadStatus status = traceDb.getLoadStatus();

        while (!status.isReady())
            TimeUnit.SECONDS.sleep(1L);

        return model.getNodeById(0).getNanos();
    }

    @VisibleForTesting
    @OnUiThread
    void loadError(final Throwable throwable)
    {
        if (!(throwable instanceof InterruptedException))
            showError("Load error", "Unable to load data", throwable);
    }

    @VisibleForTesting
    void refreshPieChart(final CountDownLatch latch)
    {
        final Supplier<Map<MatcherType, Integer>> supplier = () -> {
            try {
                return model.getMatchersByType();
            } finally {
                latch.countDown();
            }
        };

        taskRunner.compute(supplier, view::displayPieChart);
    }

    @VisibleForTesting
    void refreshTable(final CountDownLatch latch)
    {
        final Supplier<List<PerClassStatistics>> supplier = () -> {
            try {
                return model.getRulesByClass();
            } finally {
                latch.countDown();
            }
        };

        taskRunner.compute(supplier, view::displayTable);
    }
}
