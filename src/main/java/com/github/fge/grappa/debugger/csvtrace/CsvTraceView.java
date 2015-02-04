package com.github.fge.grappa.debugger.csvtrace;

import com.github.fge.grappa.debugger.csvtrace.tabs.stats.StatsTabPresenter;
import com.github.fge.grappa.debugger.csvtrace.tabs.tree.TreeTabPresenter;

public interface CsvTraceView
{
    void loadTreeTab(TreeTabPresenter presenter);

    void loadStatsTab(StatsTabPresenter statsTab);
}
