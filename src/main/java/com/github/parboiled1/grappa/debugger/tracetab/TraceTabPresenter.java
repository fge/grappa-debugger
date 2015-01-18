package com.github.parboiled1.grappa.debugger.tracetab;

import com.github.parboiled1.grappa.buffers.InputBuffer;
import com.github.parboiled1.grappa.debugger.tracetab.statistics.ParseNode;
import com.github.parboiled1.grappa.trace.ParsingRunTrace;
import com.github.parboiled1.grappa.trace.TraceEvent;
import com.google.common.base.Strings;
import org.parboiled.support.Position;

import java.util.List;

public class TraceTabPresenter
{
    private final TraceTabModel model;

    private TraceTabView view;

    public TraceTabPresenter(final TraceTabModel model)
    {
        this.model = model;
    }

    public void setView(final TraceTabView view)
    {
        this.view = view;
    }

    public void loadTrace()
    {
        final ParsingRunTrace trace = model.getTrace();
        final List<TraceEvent> events = model.getTraceEvents();

        if (events.isEmpty())
            return;

        view.setParseDate(trace.getStartDate());
        view.setTraceEvents(events);
        view.setStatistics(model.getRuleStats());
        view.setInputText(model.getInputTextInfo());
        view.setParseTree(model.getParseTreeRoot());
    }

    void handleParseNodeShow(final ParseNode node)
    {
        final InputBuffer buffer = model.getInputText();
        final int start = node.getStart();
        final Position pos = buffer.getPosition(start);
        final int line = pos.getLine();
        final int column = pos.getColumn();
        final boolean success = node.isSuccess();

        final StringBuilder sb = new StringBuilder("Match information:\n");
        sb.append("Matcher: ").append(node.getRuleName());
        sb.append("\nStarting position: line ").append(line)
            .append(", column ").append(column)
            .append("\n----\n").append(buffer.extractLine(line))
            .append('\n')
            .append(Strings.repeat(" ", column - 1)).append("^\n----\n");
        if (success) {
            sb.append("Match SUCCESS; text matched:\n<")
                .append(buffer.extract(start, node.getEnd()))
                .append('>');
        } else {
            sb.append("Match FAILED");
        }
        view.setParseNodeDetails(sb.toString());
    }
}
