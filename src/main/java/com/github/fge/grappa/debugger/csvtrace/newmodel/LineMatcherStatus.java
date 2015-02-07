package com.github.fge.grappa.debugger.csvtrace.newmodel;

public final class LineMatcherStatus
{
    private final int nrWaiting;
    private final int nrStarted;
    private final int nrSuccess;
    private final int nrFailures;

    public LineMatcherStatus(final int nrWaiting, final int nrStarted,
        final int nrSuccess, final int nrFailures)
    {
        this.nrWaiting = nrWaiting;
        this.nrStarted = nrStarted;
        this.nrSuccess = nrSuccess;
        this.nrFailures = nrFailures;
    }

    public int getNrWaiting()
    {
        return nrWaiting;
    }

    public int getNrStarted()
    {
        return nrStarted;
    }

    public int getNrSuccess()
    {
        return nrSuccess;
    }

    public int getNrFailures()
    {
        return nrFailures;
    }
}
