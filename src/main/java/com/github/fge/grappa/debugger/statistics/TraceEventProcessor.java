package com.github.fge.grappa.debugger.statistics;

import com.github.fge.grappa.trace.TraceEvent;

@FunctionalInterface
public interface TraceEventProcessor
{
    void process(TraceEvent event);
}
