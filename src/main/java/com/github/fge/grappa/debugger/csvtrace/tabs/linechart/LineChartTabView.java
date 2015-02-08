package com.github.fge.grappa.debugger.csvtrace.tabs.linechart;

import com.github.fge.grappa.debugger.csvtrace.newmodel.LineMatcherStatus;

import java.util.List;

public interface LineChartTabView
{
    void disableTabRefresh();

    void showLineMatcherStatus(List<LineMatcherStatus> list, int startLine,
        int nrLines);

    void showLoadComplete();

    void showLoadIncomplete();

    void disablePrevious();

    void disableNext();

    void enablePrevious();

    void enableNext();
}
