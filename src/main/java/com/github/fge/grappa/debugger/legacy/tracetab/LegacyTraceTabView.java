package com.github.fge.grappa.debugger.legacy.tracetab;

import com.github.fge.grappa.buffers.InputBuffer;
import com.github.fge.grappa.debugger.legacy.LegacyTraceEvent;
import com.github.fge.grappa.debugger.legacy.RuleStatistics;
import com.github.fge.grappa.debugger.statistics.ParseNode;

import java.util.Collection;
import java.util.List;

public interface LegacyTraceTabView
{
    void setTraceEvents(List<LegacyTraceEvent> events);

    void setStatistics(Collection<RuleStatistics> values);

    void setParseDate(long startDate);

    void setInputBuffer(final InputBuffer buffer);

    void setParseTree(ParseNode node);

    void fillParseNodeDetails(ParseNode node);

    void expandParseTree();

    void highlightSuccess(int start, int end);

    void highlightFailure(int end);
}
