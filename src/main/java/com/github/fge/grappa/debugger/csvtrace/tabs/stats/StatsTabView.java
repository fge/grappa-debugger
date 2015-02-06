package com.github.fge.grappa.debugger.csvtrace.tabs.stats;

import com.github.fge.grappa.debugger.common.db.RuleInvocationStatistics;
import com.github.fge.grappa.debugger.csvtrace.newmodel.ParseInfo;
import com.github.fge.grappa.matchers.MatcherType;

import java.util.List;
import java.util.Map;

public interface StatsTabView
{
    void displayParseInfo(ParseInfo info);

    void displayTotalParseTime(long totalParseTime);

    void displayMatchersByType(Map<MatcherType, Integer> matchersByType);

    void disableTableRefresh();

    void displayRuleInvocationStatistics(
        List<RuleInvocationStatistics> stats);

    default void displayInvocationStatisticsIncomplete()
    {
    }

    void displayInvocationStatisticsComplete();
}
