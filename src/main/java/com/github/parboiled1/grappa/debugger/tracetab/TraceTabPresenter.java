package com.github.parboiled1.grappa.debugger.tracetab;

import com.github.parboiled1.grappa.trace.TraceEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
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
        final TraceTabModel tmp;
        try {
            tmp = new TestTraceTabModel();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        final List<TraceEvent> events = timeRelativize(tmp.getTraceEvents());
        view.setTraceEvents(events);
    }

    private List<TraceEvent> timeRelativize(final List<TraceEvent> traceEvents)
    {
        if (traceEvents.isEmpty())
            return Collections.emptyList();

        final long startTime = traceEvents.get(0).getNanoseconds();
        final List<TraceEvent> newEvents = new ArrayList<>(traceEvents.size());

        TraceEvent newEvent;
        for (final TraceEvent event: traceEvents) {
            newEvent = new TraceEvent(event.getType(),
                event.getNanoseconds() - startTime, event.getIndex(),
                event.getMatcher(), event.getPath(), event.getLevel());
            newEvents.add(newEvent);
        }

        return Collections.unmodifiableList(newEvents);
    }
}
