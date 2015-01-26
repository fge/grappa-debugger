package com.github.fge.grappa.debugger.tracetab.stat.global;

import com.github.fge.grappa.debugger.stats.RuleMatchingStats;
import com.github.fge.grappa.trace.ParseRunInfo;

import java.util.Collection;

public interface GlobalStatsView
{
    void loadStats(Collection<RuleMatchingStats> stats);

    void loadInfo(ParseRunInfo info, int totalMatches, int treeDepth,
        long totalParseTime);

    void loadPieChart(int failedMatches, int emptyMatches, int nonEmptyMatches);
}
