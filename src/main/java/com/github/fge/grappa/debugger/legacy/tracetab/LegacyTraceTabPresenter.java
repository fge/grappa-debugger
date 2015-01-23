package com.github.fge.grappa.debugger.legacy.tracetab;

import com.github.fge.grappa.buffers.InputBuffer;
import com.github.fge.grappa.debugger.legacy.LegacyTraceEvent;
import com.github.fge.grappa.debugger.statistics.ParseNode;
import com.github.fge.grappa.debugger.statistics.TracingCharEscaper;
import com.google.common.escape.CharEscaper;
import org.parboiled.support.Position;

import java.util.ArrayList;
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
        view.setInputTextInfo(model.getInputTextInfo());
        view.setInputText(buffer.extract(0, buffer.length()));
        view.setParseTree(model.getParseTreeRoot());
    }

    void handleParseNodeShow(final ParseNode node)
    {
        final Position position = buffer.getPosition(node.getStart());
        final boolean success = node.isSuccess();

        view.fillParseNodeDetails(node, buffer);
        final List<String> fragments = success
            ? getSuccesfulMatchFragments(node)
            : getFailedMatchFragments(node);
        view.highlightText(fragments, position, success);
    }

    private List<String> getSuccesfulMatchFragments(final ParseNode node)
    {
        final int length = buffer.length();
        final int start = Math.min(length, node.getStart());
        final int end = Math.min(length, node.getEnd());

        final List<String> ret = new ArrayList<>(3);
        ret.add(buffer.extract(0, start));

        final String match = buffer.extract(start, end);
        ret.add(match.isEmpty() ? "\u2205"
            : '\u21fe' + ESCAPER.escape(match) + '\u21fd');

        ret.add(buffer.extract(end, length));

        return ret;
    }

    private List<String> getFailedMatchFragments(final ParseNode node)
    {
        final int length = buffer.length();
        final int end = Math.min(length, node.getEnd());

        final List<String> ret = new ArrayList<>(3);

        ret.add(buffer.extract(0, end));
        ret.add("\u2612");
        ret.add(buffer.extract(end, length));

        return ret;
    }

    public void handleExpandParseTree()
    {
        view.expandParseTree();
    }
}
