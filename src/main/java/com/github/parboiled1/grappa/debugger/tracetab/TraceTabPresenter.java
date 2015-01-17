package com.github.parboiled1.grappa.debugger.tracetab;

import com.github.parboiled1.grappa.debugger.tracetab.statistics.InputTextInfo;
import com.github.parboiled1.grappa.debugger.tracetab.statistics.RuleStatistics;
import com.github.parboiled1.grappa.trace.ParsingRunTrace;
import com.github.parboiled1.grappa.trace.TraceEvent;
import com.github.parboiled1.grappa.trace.TraceEventType;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
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
    private final List<TraceEvent> timedEvents = new ArrayList<>();
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
        final TraceTabModel tmp;
        try {
            tmp = new TestTraceTabModel();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        final ParsingRunTrace trace = tmp.getTrace();
        final List<TraceEvent> events = trace.getEvents();
        if (events.isEmpty())
            return;

        process(events);
        view.setParseDate(trace.getStartDate());
        view.setTraceEvents(timedEvents);
        view.setStatistics(statistics.values());
        view.setInputText(new InputTextInfo(tmp.getInputText()));
    }

    private void process(final List<TraceEvent> traceEvents)
    {
        final long traceBegin = traceEvents.get(0).getNanoseconds();

        TraceEventType type;
        long nanos;
        int start;
        String matcher;
        String path;
        int level;
        TraceEvent currentEvent;

        for (final TraceEvent traceEvent: traceEvents) {
            type = traceEvent.getType();
            nanos = traceEvent.getNanoseconds();
            start = traceEvent.getIndex();
            matcher = traceEvent.getMatcher();
            path = traceEvent.getPath();
            level = traceEvent.getLevel();

            // Add to trace events
            currentEvent = new TraceEvent(type, nanos - traceBegin, start,
                matcher, path, level);
            timedEvents.add(currentEvent);

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
