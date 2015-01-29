package com.github.fge.grappa.debugger.stats.csv;

import com.github.fge.grappa.trace.TraceEvent;

public interface TraceEventLoader
    extends Iterable<TraceEvent>, AutoCloseable
{
}
