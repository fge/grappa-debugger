package com.github.fge.grappa.debugger.csvtrace.tabs.treedepth;

import java.util.List;

public interface TreeDepthTabView
{
    void enablePrevious();

    void enableNext();

    void disableToolbar();

    void displayDepths(int startLine, int wantedLines, List<Integer> depths);

    void setMaxLines(int nrLines);

    void wakeUp();
}
