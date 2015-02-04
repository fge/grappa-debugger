package com.github.fge.grappa.debugger.csvtrace.tabs.stats;

import com.github.fge.grappa.debugger.csvtrace.newmodel.ParseInfo;

public interface StatsTabView
{
    void showParseInfo(ParseInfo info);

    void displayTotalParseTime(long totalParseTime);
}
