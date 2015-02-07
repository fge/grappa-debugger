package com.github.fge.grappa.debugger.csvtrace.tabs.matches;

import com.github.fge.grappa.debugger.common.db.RuleInvocationStatistics;

import java.util.List;

public interface MatchesTabView
{
    void disableTabRefresh();

    void showMatchesLoadingComplete();

    void showMatchesLoadingIncomplete();

    void showMatches(List<RuleInvocationStatistics> stats);
}
