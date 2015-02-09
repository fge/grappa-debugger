package com.github.fge.grappa.debugger.csvtrace.tabs.rules;

import com.github.fge.grappa.debugger.model.ParseInfo;
import com.github.fge.grappa.debugger.model.db.PerClassStatistics;
import com.github.fge.grappa.matchers.MatcherType;

import java.util.List;
import java.util.Map;

public interface RulesTabView
{
    void displayParseInfo(ParseInfo info);

    void displayTotalParseTime(long totalParseTime);

    void displayMatchersByType(Map<MatcherType, Integer> matchersByType);

    void disableRefreshRules();

    void displayRules(List<PerClassStatistics> stats);

    void hideRefreshRules();

    void enableRefreshRules();
}
