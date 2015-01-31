package com.github.fge.grappa.debugger.csvtrace.model;

import com.github.fge.grappa.exceptions.GrappaException;
import com.github.fge.grappa.run.EventBasedParseRunner;
import com.github.fge.grappa.run.ParseRunner;
import com.github.fge.grappa.trace.TraceEvent;
import com.github.fge.grappa.trace.parser.TraceEventParser;
import org.parboiled.Parboiled;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;

@ParametersAreNonnullByDefault
final class BatchEventsReader
{
    private final int totalSize;
    private final int batchSize;
    private final TraceEventParser parser;
    private final ParseRunner<TraceEvent> runner;
    private final BlockingQueue<String> inputQueue;
    private final BlockingQueue<TraceEvent> eventQueue;

    BatchEventsReader(final int batchSize, final int totalSize,
        final BlockingQueue<String> inputQueue,
        final BlockingQueue<TraceEvent> eventQueue)
    {
        this.inputQueue = Objects.requireNonNull(inputQueue);
        this.eventQueue = Objects.requireNonNull(eventQueue);

        parser = Parboiled.createParser(TraceEventParser.class, eventQueue);
        runner = new EventBasedParseRunner<>(parser.traceEvents(batchSize));

        this.totalSize = totalSize;
        this.batchSize = batchSize;
    }

    void process()
        throws InterruptedException
    {
        int remaining = totalSize;
        boolean success;

        String input;
        while (remaining >= batchSize) {
            input = inputQueue.take();
            success = runner.run(input).isSuccess();
            if (!success)
                throw new GrappaException("failed to parse events (remaining: "
                    + remaining + ")");
            remaining -= batchSize;
        }

        /*
         * Create another runner for the rest of the events
         */
        final ParseRunner<TraceEvent> runner2
            = new EventBasedParseRunner<>(parser.traceEvents(remaining));

        input = inputQueue.take();
        success = runner2.run(input).isSuccess();
        if (!success)
            throw new GrappaException("failed to parse events (remaining: "
                + remaining + ")");
    }
}
