package com.github.fge.grappa.debugger.csvtrace.tabs.rules;

import com.github.fge.grappa.debugger.GrappaDebuggerException;
import com.github.fge.grappa.debugger.common.GuiTaskRunner;
import com.github.fge.grappa.debugger.csvtrace.CsvTraceModel;
import com.github.fge.grappa.debugger.javafx.TabPresenter;
import com.github.fge.grappa.debugger.mainwindow.MainWindowView;
import com.github.fge.grappa.debugger.model.ParseInfo;
import com.github.fge.grappa.debugger.model.db.PerClassStatistics;
import com.github.fge.grappa.matchers.MatcherType;
import com.google.common.annotations.VisibleForTesting;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class RulesTabPresenter
    extends TabPresenter<RulesTabView>
{
    private final MainWindowView mainView;
    private final CsvTraceModel model;

    public RulesTabPresenter(final GuiTaskRunner taskRunner,
        final MainWindowView mainView, final CsvTraceModel model)
    {
        super(taskRunner);
        this.mainView = mainView;
        this.model = model;
    }

    @Override
    public void load()
    {
        loadParseInfo();
        loadTotalParseTime();
        refresh();
    }

    @Override
    public CountDownLatch refresh()
    {
        final CountDownLatch latch = new CountDownLatch(2);

        refreshMatchersByType(latch);
        refreshRulesByClass(latch);

        return latch;
    }

    @VisibleForTesting
    void refreshMatchersByType(final CountDownLatch latch)
    {
        taskRunner.computeOrFail(
            () -> doGetMatchersByType(latch),
            view::displayMatchersByType,
            this::handleLoadMatchersByTypeError
        );
    }

    @VisibleForTesting
    Map<MatcherType, Integer> doGetMatchersByType(final CountDownLatch latch)
        throws GrappaDebuggerException
    {
        try {
            return model.getMatchersByType();
        } finally {
            latch.countDown();
        }
    }

    @VisibleForTesting
    void handleLoadMatchersByTypeError(final Throwable throwable)
    {
        mainView.showError("Load error", "Unable to load matcher statistics",
            throwable);
    }

    @VisibleForTesting
    void refreshRulesByClass(final CountDownLatch latch)
    {
        taskRunner.computeOrFail(
            () -> doGetRulesByClass(latch),
            view::displayRules,
            this::handleRefreshRulesError
        );
    }

    @VisibleForTesting
    List<PerClassStatistics> doGetRulesByClass(final CountDownLatch latch)
        throws GrappaDebuggerException
    {
        try {
            return model.getRulesByClass();
        } finally {
            latch.countDown();
        }
    }

    @VisibleForTesting
    void handleRefreshRulesError(final Throwable throwable)
    {
        mainView.showError("Database error", "Unable to refresh rules",
            throwable);
    }

    @VisibleForTesting
    void loadParseInfo()
    {
        final ParseInfo info = model.getParseInfo();
        view.displayParseInfo(info);
    }

    @VisibleForTesting
    void loadTotalParseTime()
    {
        taskRunner.computeOrFail(() -> model.getNodeById(0).getNanos(),
            view::displayTotalParseTime, this::handleLoadTotalParseTimeError);
    }

    @VisibleForTesting
    void handleLoadTotalParseTimeError(final Throwable throwable)
    {
       mainView.showError("Load error", "Unable to load parse time", throwable);
    }
}
