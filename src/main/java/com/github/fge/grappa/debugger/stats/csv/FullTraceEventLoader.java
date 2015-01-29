package com.github.fge.grappa.debugger.stats.csv;

import com.github.fge.grappa.exceptions.GrappaException;
import com.github.fge.grappa.run.EventBasedParseRunner;
import com.github.fge.grappa.run.ParseRunner;
import com.github.fge.grappa.trace.TraceEvent;
import com.github.fge.grappa.trace.parser.TraceEventParser;
import com.google.common.eventbus.Subscribe;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.parboiled.Parboiled;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;

final class FullTraceEventLoader
    implements TraceEventLoader, Closeable, Iterator<TraceEvent>
{
    private static final ThreadFactory THREAD_FACTORY
        = new ThreadFactoryBuilder().setNameFormat("csv-fullread-%d")
        .setDaemon(true).build();

    private final ExecutorService executor
        = Executors.newSingleThreadExecutor(THREAD_FACTORY);

    private final BlockingQueue<String> queue
        = new ArrayBlockingQueue<>(16384);

    private final BufferedReader reader;

    private final TraceEventParser parser
        = Parboiled.createParser(TraceEventParser.class);
    private final ParseRunner<Void> runner
        = new EventBasedParseRunner<>(parser.traceEvent());

    private final AtomicBoolean alreadyCalled = new AtomicBoolean(false);

    private volatile TraceEvent event;

    FullTraceEventLoader(final BufferedReader reader)
    {
        this.reader = Objects.requireNonNull(reader);
        executor.submit(() -> {
            String line;
            try {
                while((line = reader.readLine()) != null)
                    queue.put(line);
            } catch (InterruptedException e) {
                throw new GrappaException("interruped!", e);
            } catch (IOException e) {
                throw new GrappaException("error while reading CSV", e);
            }
        });
    }

    @Subscribe
    public void receiveEvent(final TraceEvent event)
    {
        this.event = event;
    }

    @Override
    public Iterator<TraceEvent> iterator()
    {
        if (alreadyCalled.getAndSet(true))
            throw new UnsupportedOperationException();
        parser.register(this);
        return this;
    }

    @Override
    public boolean hasNext()
    {
        return queue.peek() == null;
    }

    /**
     * Returns the next element in the iteration.
     *
     * @return the next element in the iteration
     *
     * @throws NoSuchElementException if the iteration has no more elements
     */
    @Override
    public TraceEvent next()
    {
        if (!runner.run(queue.remove()).isSuccess())
            throw new GrappaException("illegal content in trace file");
        return event;
    }

    @Override
    public void close()
        throws IOException
    {
        reader.close();
    }

}
