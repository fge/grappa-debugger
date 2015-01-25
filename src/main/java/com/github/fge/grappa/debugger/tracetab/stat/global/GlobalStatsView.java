package com.github.fge.grappa.debugger.tracetab.stat.global;

import com.github.fge.grappa.debugger.statistics.RuleMatchingStats;

import java.util.Collection;

public interface GlobalStatsView
{
    void loadStats(Collection<RuleMatchingStats> stats);
}
