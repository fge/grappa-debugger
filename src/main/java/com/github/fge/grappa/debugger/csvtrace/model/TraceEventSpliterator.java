package com.github.fge.grappa.debugger.csvtrace.model;

import com.github.fge.grappa.exceptions.GrappaException;
import com.github.fge.grappa.trace.TraceEvent;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

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
    private static final int BATCH_SIZE = 512;
    private static final int QUEUE_SIZE = 1024;
    private static final ThreadFactory THREAD_FACTORY
        = new ThreadFactoryBuilder().setDaemon(true)
        .setNameFormat("events-spliterator-%d").build();

    private final ExecutorService executor
        = Executors.newFixedThreadPool(2, THREAD_FACTORY);

    private final BlockingQueue<String> lineQueue
        = new LinkedBlockingQueue<>(QUEUE_SIZE);
    private final BlockingQueue<TraceEvent> eventQueue
        = new LinkedBlockingQueue<>(QUEUE_SIZE);

    private final BatchLineReader lineReader;
    private final BatchEventsReader eventsReader;

    private long count = 0L;
    private final long nrEvents;

    public TraceEventSpliterator(final BufferedReader reader,
        final int nrEvents)
    {
        Objects.requireNonNull(reader);
        this.nrEvents = (long) nrEvents;

        lineReader = new BatchLineReader(nrEvents, BATCH_SIZE, lineQueue,
            reader);
        eventsReader = new BatchEventsReader(BATCH_SIZE, nrEvents, lineQueue,
            eventQueue);

        executor.submit(() -> {
            try {
                lineReader.process();
            } catch (IOException e) {
                throw new GrappaException("failed to read events file", e);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new GrappaException("parsing interrupted", e);
            }
        });
        executor.submit(() -> {
            try {
                eventsReader.process();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new GrappaException("parsing interrupted", e);
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
