package com.github.parboiled1.grappa.debugger.tracetab;

import com.github.parboiled1.grappa.debugger.tracetab.statistics.RuleStatistics;
import com.github.parboiled1.grappa.trace.ParsingRunTrace;
import com.github.parboiled1.grappa.trace.TraceEvent;
import com.github.parboiled1.grappa.trace.TraceEventType;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TraceTabPresenter
{
    private final TraceTabModel model;

    private TraceTabView view;

    private final Map<String, RuleStatistics> statistics
        = new LinkedHashMap<>();
    private final Deque<TraceEvent> eventStack = new ArrayDeque<>();

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
        final List<TraceEvent> events = trace.getEvents();
        if (events.isEmpty())
            return;

        process(events);
        view.setParseDate(trace.getStartDate());
        view.setTraceEvents(model.getTraceEvents());
        view.setStatistics(statistics.values());
        view.setInputText(model.getInputTextInfo());
    }

    private void process(final List<TraceEvent> traceEvents)
    {
        TraceEventType type;
        long nanos;
        String matcher;

        for (final TraceEvent traceEvent: traceEvents) {
            type = traceEvent.getType();
            nanos = traceEvent.getNanoseconds();
            matcher = traceEvent.getMatcher();


            if (type == TraceEventType.BEFORE_MATCH) {
                eventStack.push(traceEvent);
                statistics.computeIfAbsent(matcher, RuleStatistics::new);
                continue;
            }

            final boolean success = type == TraceEventType.MATCH_SUCCESS;
            final TraceEvent startEvent = eventStack.pop();
            final RuleStatistics stats = statistics.get(matcher);
            stats.addInvocation(nanos - startEvent.getNanoseconds(), success);
        }
    }
}
