package com.github.fge.grappa.debugger.legacy.tracetab;

import com.github.fge.grappa.buffers.InputBuffer;
import com.github.fge.grappa.debugger.legacy.LegacyTraceEvent;
import com.github.fge.grappa.debugger.statistics.ParseNode;
import com.github.fge.grappa.debugger.statistics.TracingCharEscaper;
import com.google.common.escape.CharEscaper;

import java.util.List;

public class LegacyTraceTabPresenter
{
    private static final CharEscaper ESCAPER = new TracingCharEscaper();

    private final LegacyTraceTabModel model;
    private final InputBuffer buffer;

    private LegacyTraceTabView view;

    public LegacyTraceTabPresenter(final LegacyTraceTabModel model)
    {
        this.model = model;
        buffer = model.getInputBuffer();
    }

    public void setView(final LegacyTraceTabView view)
    {
        this.view = view;
    }

    public void loadTrace()
    {
        final List<LegacyTraceEvent> events = model.getTraceEvents();

        view.setParseDate(model.getTrace().getStartDate());
        view.setTraceEvents(events);
        view.setStatistics(model.getRuleStats());
        view.setInputBuffer(buffer);
        view.setParseTree(model.getParseTreeRoot());
    }

    void handleParseNodeShow(final ParseNode node)
    {
        view.fillParseNodeDetails(node);
        if (node.isSuccess())
            view.highlightSuccess(node.getStart(), node.getEnd());
        else
            view.highlightFailure(node.getEnd());
    }

    public void handleExpandParseTree()
    {
        view.expandParseTree();
    }
}
