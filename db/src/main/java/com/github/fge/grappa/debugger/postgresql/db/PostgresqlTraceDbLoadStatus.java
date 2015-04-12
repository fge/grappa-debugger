package com.github.fge.grappa.debugger.postgresql.db;

import com.github.fge.grappa.debugger.TraceDbLoadStatus;

public final class PostgresqlTraceDbLoadStatus
    implements TraceDbLoadStatus
{
    @Override
    public boolean isReady()
    {
        return true;
    }

    @Override
    public int getLoadedMatchers()
    {
        // TODO
        return 0;
    }

    @Override
    public int getLoadedNodes()
    {
        // TODO
        return 0;
    }
}
