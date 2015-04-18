package com.github.fge.grappa.debugger.trace.tabs.rules;

import com.github.fge.grappa.debugger.model.rules.PerClassStatistics;
import com.github.fge.grappa.debugger.trace.tabs.TabView;
import com.github.fge.grappa.matchers.MatcherType;

import java.util.List;
import java.util.Map;

public interface RulesTabView
    extends TabView
{
    void displayParseTime(long nanos);

    void displayPieChart(Map<MatcherType, Integer> map);

    void displayTable(List<PerClassStatistics> list);
}
