package com.github.fge.grappa.debugger.h2.db.load;

import com.github.fge.grappa.debugger.TraceDbLoadStatus;
import com.github.fge.grappa.debugger.model.TraceModelException;

import java.util.concurrent.atomic.AtomicReference;

public final class H2TraceDbLoadStatus
    implements TraceDbLoadStatus
{
    private final AtomicReference<Throwable> loadError;

    private volatile boolean ready = false;
    private volatile int loadedMatchers = 0;
    private volatile int loadedNodes = 0;

    public H2TraceDbLoadStatus(final AtomicReference<Throwable> loadError)
    {
        this.loadError = loadError;
    }

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
        final Throwable throwable = loadError.get();

        if (throwable == null)
            return ready;

        if (throwable instanceof Error)
            throw (Error) throwable;
        if (throwable instanceof RuntimeException)
            throw (RuntimeException) throwable;
        throw new TraceModelException(throwable);
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
