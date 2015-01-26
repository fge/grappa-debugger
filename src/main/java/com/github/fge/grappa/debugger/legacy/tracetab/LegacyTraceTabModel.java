package com.github.fge.grappa.debugger.legacy.tracetab;

import com.github.fge.grappa.buffers.InputBuffer;
import com.github.fge.grappa.debugger.legacy.stats.LegacyParseNode;
import com.github.fge.grappa.debugger.legacy.stats.LegacyTraceEvent;
import com.github.fge.grappa.debugger.legacy.stats.ParsingRunTrace;
import com.github.fge.grappa.debugger.legacy.stats.RuleStatistics;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;

public interface LegacyTraceTabModel
{
    @Nonnull
    ParsingRunTrace getTrace();

    @Nonnull
    InputBuffer getInputBuffer();

    @Nonnull
    List<LegacyTraceEvent> getTraceEvents();

    @Nonnull
    Collection<RuleStatistics> getRuleStats();

    @Nonnull
    LegacyParseNode getParseTreeRoot();

    int getNrEmptyMatches();
}
