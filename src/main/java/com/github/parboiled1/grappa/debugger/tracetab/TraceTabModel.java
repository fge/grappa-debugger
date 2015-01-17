package com.github.parboiled1.grappa.debugger.tracetab;

import com.github.parboiled1.grappa.buffers.InputBuffer;
import com.github.parboiled1.grappa.debugger.tracetab.statistics.InputTextInfo;
import com.github.parboiled1.grappa.debugger.tracetab.statistics.RuleStatistics;
import com.github.parboiled1.grappa.trace.ParsingRunTrace;
import com.github.parboiled1.grappa.trace.TraceEvent;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;

public interface TraceTabModel
{
    @Nonnull
    ParsingRunTrace getTrace();

    @Nonnull
    InputBuffer getInputText();

    @Nonnull
    List<TraceEvent> getTraceEvents();

    @Nonnull
    InputTextInfo getInputTextInfo();

    @Nonnull
    Collection<RuleStatistics> getRuleStats();
}
