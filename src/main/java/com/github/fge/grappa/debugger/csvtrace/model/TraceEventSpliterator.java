package com.github.fge.grappa.debugger.csvtrace.model;

import com.github.fge.grappa.exceptions.GrappaException;
import com.github.fge.grappa.run.EventBasedParseRunner;
import com.github.fge.grappa.run.ParseRunner;
import com.github.fge.grappa.trace.TraceEvent;
import com.github.fge.grappa.trace.parser.TraceEventBuilder;
import com.github.fge.grappa.trace.parser.TraceEventParser;
import org.parboiled.Parboiled;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Objects;
import java.util.Spliterator;
import java.util.function.Consumer;

@ParametersAreNonnullByDefault
public final class TraceEventSpliterator
    implements Spliterator<TraceEvent>
{
    private final TraceEventBuilder builder = new TraceEventBuilder();
    private final TraceEventParser parser
        = Parboiled.createParser(TraceEventParser.class, builder);
    private final ParseRunner<TraceEvent> runner
        = new EventBasedParseRunner<>(parser.traceEvent());

    private final BufferedReader reader;

    private long count = 0L;

    public TraceEventSpliterator(final BufferedReader reader)
    {
        this.reader = Objects.requireNonNull(reader);
    }

    @Override
    public boolean tryAdvance(final Consumer<? super TraceEvent> action)
    {
        Objects.requireNonNull(action);
        final String line;
        try {
            line = reader.readLine();
        } catch (IOException e) {
            throw new GrappaException("failed to read line (current line was "
                + count + ")", e);
        }
        if (line == null)
            return false;
        count++;
        if (!runner.run(line).isSuccess())
            throw new GrappaException("failed to parse event at line " + count);
        action.accept(builder.build());
        return true;
    }

    @Override
    public Spliterator<TraceEvent> trySplit()
    {
        return null;
    }

    @Override
    public long estimateSize()
    {
        return Long.MAX_VALUE;
    }

    @Override
    public int characteristics()
    {
        return IMMUTABLE | DISTINCT | ORDERED;
    }
}
