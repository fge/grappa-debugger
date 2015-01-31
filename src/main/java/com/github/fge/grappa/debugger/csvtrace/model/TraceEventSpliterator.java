package com.github.fge.grappa.debugger.csvtrace.model;

import com.github.fge.grappa.exceptions.GrappaException;
import com.github.fge.grappa.run.EventBasedParseRunner;
import com.github.fge.grappa.run.ParseRunner;
import com.github.fge.grappa.trace.TraceEvent;
import com.github.fge.grappa.trace.parser.TraceEventParser;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.parboiled.Parboiled;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Objects;
import java.util.Spliterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.function.Consumer;

@ParametersAreNonnullByDefault
public final class TraceEventSpliterator
    implements Spliterator<TraceEvent>
{
    private static final int QUEUE_SIZE = 32768;
    private static final ThreadFactory THREAD_FACTORY
        = new ThreadFactoryBuilder().setDaemon(true)
        .setNameFormat("events-spliterator-%d").build();

    private final ExecutorService executor
        = Executors.newFixedThreadPool(2, THREAD_FACTORY);

    private final BlockingQueue<String> lineQueue
        = new LinkedBlockingQueue<>(QUEUE_SIZE);
    private final BlockingQueue<TraceEvent> eventQueue
        = new LinkedBlockingQueue<>(QUEUE_SIZE);

    private long count = 0L;
    private final long nrEvents;

    public TraceEventSpliterator(final BufferedReader reader,
        final int nrEvents)
    {
        Objects.requireNonNull(reader);
        this.nrEvents = (long) nrEvents;

        final TraceEventParser parser
            = Parboiled.createParser(TraceEventParser.class, eventQueue);
        final ParseRunner<TraceEvent> runner
            = new EventBasedParseRunner<>(parser.traceEvent());

        executor.submit(() -> {
            for (long lineCount = 0; lineCount < nrEvents; lineCount++)
                try {
                    lineQueue.put(reader.readLine());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new GrappaException("parsing interruped!", e);
                } catch (IOException e) {
                    throw new GrappaException("failed to read line ("
                        + lineCount + " lines read so far)");
                }
        });
        executor.submit(() -> {
            for (long eventCount = 0; eventCount < nrEvents; eventCount++)
                try {
                    final String input = lineQueue.take();
                    if (!runner.run(input).isSuccess())
                        throw new GrappaException("failed to parse event (line:"
                            + eventCount + ")");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new GrappaException("parsing interruped!", e);
                }
        });
    }

    @Override
    public boolean tryAdvance(final Consumer<? super TraceEvent> action)
    {
        Objects.requireNonNull(action);

        if (count == nrEvents)
            return false;

        try {
            action.accept(eventQueue.take());
            count++;
            if (count % 25000L == 0)
                System.out.println(count + " events processed");
            return true;
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
            throw new GrappaException("parsing interrupted");
        }
    }

    @Override
    public Spliterator<TraceEvent> trySplit()
    {
        return null;
    }

    @Override
    public long estimateSize()
    {
        return nrEvents - count;
    }

    @Override
    public int characteristics()
    {
        return IMMUTABLE | DISTINCT | ORDERED | SIZED;
    }
}
