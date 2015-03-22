package com.github.fge.grappa.debugger.database;

public interface TraceLoadStatus
{
    int getTotalMatchers();

    int getLoadedMatchers();

    int getTotalNodes();

    int getLoadedNodes();

    boolean isLoaded();
}
