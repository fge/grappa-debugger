package com.github.fge.grappa.debugger.stats.csv;

import com.github.fge.grappa.exceptions.GrappaException;
import com.github.fge.grappa.internal.NonFinalForTesting;
import com.github.fge.grappa.rules.Rule;
import com.github.fge.grappa.run.EventBasedParseRunner;
import com.github.fge.grappa.trace.parser.TraceEventCollector;
import com.github.fge.grappa.trace.parser.TraceEventParser;
import org.parboiled.Parboiled;

import javax.annotation.WillNotClose;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.stream.Stream;

import static java.nio.charset.StandardCharsets.UTF_8;

@NonFinalForTesting
public class TraceEventLoaderFactory
{
    private final Path traceFile;

    public TraceEventLoaderFactory(final Path traceFile)
    {
        this.traceFile = Objects.requireNonNull(traceFile);
    }

    public TraceEventLoader fullLoader()
        throws IOException
    {
        @WillNotClose
        final BufferedReader reader
            = Files.newBufferedReader(traceFile, UTF_8);
        return new FullTraceEventLoader(reader);
    }

    public TraceEventLoader boundedLoader(final int quantity)
        throws IOException
    {
        final TraceEventCollector listener = new TraceEventCollector();

        if (quantity < 0)
            throw new IllegalArgumentException("quantity cannot be negative");

        final StringBuilder sb = new StringBuilder();

        try (
            final Stream<String> lines = Files.lines(traceFile, UTF_8);
        ) {
            lines.limit(quantity).forEach(line -> {
                sb.append(line); sb.append('\n');
            });
        }

        final TraceEventParser parser
            = Parboiled.createParser(TraceEventParser.class, quantity);
        final Rule rule = parser.traceEvents();
        parser.register(listener);
        final EventBasedParseRunner<Object> runner
            = new EventBasedParseRunner<>(rule);
        if (!runner.run(sb).isSuccess())
            throw new GrappaException("illegal content in trace file");
        return new BoundedTraceEventLoader(listener.getEvents());
    }
}
