package com.github.fge.grappa.debugger.csvtrace.tabs.treedepth;

import com.github.fge.grappa.debugger.csvtrace.tabs.TabView;

import java.util.Map;

public interface TreeDepthTabView
    extends TabView
{
    void disableToolbar();

    void displayChart(Map<Integer, Integer> depthMap);

    void updateStartLine(int startLine);

    void updateToolbar(boolean disablePrev, boolean disableNext);
}
