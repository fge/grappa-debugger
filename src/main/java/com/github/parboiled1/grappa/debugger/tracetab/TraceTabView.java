package com.github.parboiled1.grappa.debugger.tracetab;

import com.github.parboiled1.grappa.trace.TraceEvent;

import java.util.List;

public interface TraceTabView
{
    void setTraceEvents(List<TraceEvent> events);
}
