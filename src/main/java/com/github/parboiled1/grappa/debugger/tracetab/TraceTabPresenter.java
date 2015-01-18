package com.github.parboiled1.grappa.debugger.tracetab;

import com.github.parboiled1.grappa.buffers.InputBuffer;
import com.github.parboiled1.grappa.debugger.tracetab.statistics.InputTextInfo;
import com.github.parboiled1.grappa.debugger.tracetab.statistics.ParseNode;
import com.github.parboiled1.grappa.debugger.tracetab.statistics.RuleStatistics;
import com.github.parboiled1.grappa.trace.ParsingRunTrace;
import com.github.parboiled1.grappa.trace.TraceEvent;
import com.google.common.base.Strings;
import org.parboiled.support.Position;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TraceTabPresenter
{
    private final ParsingRunTrace trace;
    private final List<TraceEvent> events;
    private final Collection<RuleStatistics> ruleStats;
    private final InputBuffer buffer;
    private final InputTextInfo textInfo;
    private final ParseNode parseTreeRoot;

    private TraceTabView view;

    public TraceTabPresenter(final TraceTabModel model)
    {
        trace = model.getTrace();
        events = model.getTraceEvents();
        ruleStats = model.getRuleStats();
        buffer = model.getInputBuffer();
        textInfo = model.getInputTextInfo();
        parseTreeRoot = model.getParseTreeRoot();
    }

    public void setView(final TraceTabView view)
    {
        this.view = view;
    }

    public void loadTrace()
    {
        if (events.isEmpty())
            return;

        view.setParseDate(trace.getStartDate());
        view.setTraceEvents(events);
        view.setStatistics(ruleStats);
        view.setInputTextInfo(textInfo);
        view.setInputText(buffer.extract(0, buffer.length()));
        view.setParseTree(parseTreeRoot);
    }

    void handleParseNodeShow(final ParseNode node)
    {
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
        final List<String> fragments = getFragments(node);
        view.highlightText(fragments);
    }

    private List<String> getFragments(final ParseNode node)
    {
        final int length = buffer.length();
        final int start = node.getStart();
        final int end = node.getEnd();

        final List<String> ret = new ArrayList<>(3);
        ret.add(buffer.extract(0, start));
        final String match = buffer.extract(Math.min(start, length), end);
        ret.add(match.isEmpty() ? "" : '<' + match + '>');
        ret.add(buffer.extract(Math.min(end, length), length));

        return ret;
    }
}
