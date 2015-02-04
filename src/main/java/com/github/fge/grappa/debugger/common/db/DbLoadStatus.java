package com.github.fge.grappa.debugger.common.db;

import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public final class DbLoadStatus
{
    private final CountDownLatch matchersLatch = new CountDownLatch(1);
    private final CountDownLatch readyLatch = new CountDownLatch(1);

    private final int nrMatchers;
    private final int nrNodes;

    private int processedMatchers = 0;
    private int processedNodes = 0;

    public DbLoadStatus(final int nrMatchers, final int nrNodes)
    {
        this.nrMatchers = nrMatchers;
        this.nrNodes = nrNodes;
    }

    public int getTotal()
    {
        return nrMatchers + nrNodes;
    }

    public int getCurrent()
    {
        return processedMatchers + processedNodes;
    }

    public int getProcessedMatchers()
    {
        return processedMatchers;
    }

    public int getProcessedNodes()
    {
        return processedNodes;
    }

    void incrementProcessedMatchers()
    {
        processedMatchers++;
        if (processedMatchers == nrMatchers)
            matchersLatch.countDown();
    }

    void incrementProcessedNodes()
    {
        processedNodes++;
    }

    public boolean waitReady(final long count, final TimeUnit unit)
        throws InterruptedException
    {
        Objects.requireNonNull(unit);
        return readyLatch.await(count, unit);
    }

    void setReady()
    {
        readyLatch.countDown();
    }

    public void waitForMatchers()
        throws InterruptedException
    {
        matchersLatch.await();
    }

    public void waitForNodes()
        throws InterruptedException
    {
        readyLatch.await();
    }
}
