package com.github.fge.grappa.debugger.h2.db.load;

import com.github.fge.grappa.debugger.TraceDbLoadStatus;

public final class H2TraceDbLoadStatus
    implements TraceDbLoadStatus
{
    private volatile boolean ready = false;
    private volatile int loadedMatchers = 0;
    private volatile int loadedNodes = 0;

    void setReady()
    {
        ready = true;
    }

    void incrementProcessedMatchers()
    {
        loadedMatchers++;
    }

    void incrementProcessedNodes()
    {
        loadedNodes++;
    }

    @Override
    public boolean isReady()
    {
        return ready;
    }

    @Override
    public int getLoadedMatchers()
    {
        return loadedMatchers;
    }

    @Override
    public int getLoadedNodes()
    {
        return loadedNodes;
    }
}
