package com.github.fge.grappa.debugger.legacy.tracetab;

import com.github.fge.grappa.buffers.InputBuffer;
import com.github.fge.grappa.debugger.legacy.stats.LegacyTraceEvent;
import com.github.fge.grappa.debugger.legacy.stats.RuleStatistics;
import com.github.fge.grappa.debugger.stats.ParseNode;

import java.util.Collection;
import java.util.List;

public interface LegacyTraceTabView
{
    void setTraceEvents(List<LegacyTraceEvent> events);

    void setStatistics(Collection<RuleStatistics> values, int nrEmptyMatches);

    void setParseDate(long startDate);

    void setInputBuffer(final InputBuffer buffer);

    void setParseTree(ParseNode node);

    void fillParseNodeDetails(ParseNode node);

    void expandParseTree();

    void highlightSuccess(int start, int end);

    void highlightFailure(int end);
}
