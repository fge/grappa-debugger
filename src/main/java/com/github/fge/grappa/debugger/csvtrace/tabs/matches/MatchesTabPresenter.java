package com.github.fge.grappa.debugger.csvtrace.tabs.matches;

import com.github.fge.grappa.debugger.common.GuiTaskRunner;
import com.github.fge.grappa.debugger.csvtrace.CsvTraceModel;
import com.github.fge.grappa.debugger.javafx.BasePresenter;
import com.github.fge.grappa.debugger.mainwindow.MainWindowView;
import com.github.fge.grappa.debugger.model.db.RuleInvocationStatistics;
import com.google.common.annotations.VisibleForTesting;

import javax.annotation.Nonnull;
import java.util.List;

public class MatchesTabPresenter
    extends BasePresenter<MatchesTabView>
{
    private final GuiTaskRunner taskRunner;
    private final CsvTraceModel model;
    private final MainWindowView mainView;

    public MatchesTabPresenter(final GuiTaskRunner taskRunner,
        final CsvTraceModel model, final MainWindowView mainView)
    {
        this.taskRunner = taskRunner;
        this.model = model;
        this.mainView = mainView;
    }

    public void load()
    {
        handleTabRefresh();
    }

    @VisibleForTesting
    void handleTabRefreshError(final Throwable throwable)
    {
        mainView.showError("Load error", "Unable to load matcher statistics",
            throwable);
    }

    @VisibleForTesting
    void handleTabRefresh()
    {
        final boolean complete = model.isLoadComplete();

        taskRunner.computeOrFail(
            view::disableTabRefresh,
            this::getMatchersData,
            data -> {
                updateTab(data);
                final Runnable postRefresh = complete
                    ? view::showMatchesLoadingComplete
                    : view::showMatchesLoadingIncomplete;

                taskRunner.executeFront(postRefresh);
            },
            this::handleTabRefreshError
        );

    }

    @VisibleForTesting
    MatchersData getMatchersData()
    {
        return new MatchersData(model);
    }

    @VisibleForTesting
    void updateTab(final MatchersData data)
    {
        view.showMatches(data.getMatches());
        view.showMatchesStats(data.getNonEmpty(), data.getEmpty(),
            data.getFailures());
        final int total = data.getTotal();
        view.showTopOne(data.getTopOne(), total);
        view.showTopFive(data.getTopFive(), total);
        view.showTopTen(data.getTopTen(), total);
    }

    @VisibleForTesting
    static class MatchersData
    {
        private final List<RuleInvocationStatistics> matches;
        private final int nonEmpty;
        private final int empty;
        private final int failures;
        private final int total;
        private final Integer topOne;
        private final Integer topFive;
        private final Integer topTen;

        private MatchersData(@Nonnull final CsvTraceModel model)
        {
            matches = model.getMatches();
            nonEmpty = matches.stream()
                .mapToInt(RuleInvocationStatistics::getNonEmptyMatches)
                .sum();
            empty = matches.stream()
                .mapToInt(RuleInvocationStatistics::getEmptyMatches)
                .sum();
            failures = matches.stream()
                .mapToInt(RuleInvocationStatistics::getFailedMatches)
                .sum();

            total = nonEmpty + empty + failures;

            final List<Integer> top = model.getTopMatcherCount();

            final int size = top.size();

            topOne = size >= 1 ? top.get(0) : null;
            topFive = size >= 5
                ? top.stream().mapToInt(Integer::intValue).limit(5).sum()
                : null;
            topTen = size >= 10
                ? top.stream().mapToInt(Integer::intValue).sum()
                : null;
        }

        List<RuleInvocationStatistics> getMatches()
        {
            return matches;
        }

        int getNonEmpty()
        {
            return nonEmpty;
        }

        int getEmpty()
        {
            return empty;
        }

        int getFailures()
        {
            return failures;
        }

        int getTotal()
        {
            return total;
        }

        Integer getTopOne()
        {
            return topOne;
        }

        Integer getTopFive()
        {
            return topFive;
        }

        Integer getTopTen()
        {
            return topTen;
        }
    }
}
