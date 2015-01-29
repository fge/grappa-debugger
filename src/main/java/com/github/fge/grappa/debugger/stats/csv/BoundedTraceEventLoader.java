package com.github.fge.grappa.debugger.stats.csv;

import com.github.fge.grappa.trace.TraceEvent;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

final class BoundedTraceEventLoader
    implements TraceEventLoader
{
    private final List<TraceEvent> events;

    BoundedTraceEventLoader(final List<TraceEvent> events)
    {
        this.events = Objects.requireNonNull(events);
    }

    /**
     * Returns an iterator over elements of type {@code T}.
     *
     * @return an Iterator.
     */
    @Override
    public Iterator<TraceEvent> iterator()
    {
        return events.iterator();
    }

    @Override
    public void close()
    {
    }
}
