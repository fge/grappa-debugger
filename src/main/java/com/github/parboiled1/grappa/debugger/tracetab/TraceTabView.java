package com.github.parboiled1.grappa.debugger.tracetab;

import com.github.parboiled1.grappa.debugger.tracetab.statistics.RuleStatistics;
import com.github.parboiled1.grappa.trace.TraceEvent;

import java.util.Collection;
import java.util.List;

public interface TraceTabView
{
    void setTraceEvents(List<TraceEvent> events);

    void setStatistics(Collection<RuleStatistics> values);
}
