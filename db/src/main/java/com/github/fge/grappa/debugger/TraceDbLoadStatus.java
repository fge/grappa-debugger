package com.github.fge.grappa.debugger;

public interface TraceDbLoadStatus
{
    boolean isReady();

    int getLoadedMatchers();

    int getLoadedNodes();
}
