package com.github.fge.grappa.debugger.csvtrace.tabs.matches;

public interface MatchesTabView
{
    void disableTabRefresh();

    void displayInvocationStatisticsComplete();

    void displayInvocationStatisticsIncomplete();

    void displayRuleInvocationStatistics(Object stats);
}
