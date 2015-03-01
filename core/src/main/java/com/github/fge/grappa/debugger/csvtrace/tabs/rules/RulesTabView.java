package com.github.fge.grappa.debugger.csvtrace.tabs.rules;

import com.github.fge.grappa.debugger.model.common.ParseInfo;
import com.github.fge.grappa.debugger.model.tabs.rules.PerClassStatistics;
import com.github.fge.grappa.matchers.MatcherType;

import java.util.List;
import java.util.Map;

public interface RulesTabView
{
    void displayParseInfo(ParseInfo info);

    void displayTotalParseTime(long totalParseTime);

    void displayMatchersByType(Map<MatcherType, Integer> matchersByType);

    void displayRules(List<PerClassStatistics> stats);
}
