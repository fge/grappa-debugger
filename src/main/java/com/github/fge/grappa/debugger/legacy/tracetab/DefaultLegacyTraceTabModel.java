package com.github.fge.grappa.debugger.legacy.tracetab;

import com.fasterxml.jackson.core.JsonGenerator.Feature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.grappa.buffers.CharSequenceInputBuffer;
import com.github.fge.grappa.buffers.InputBuffer;
import com.github.fge.grappa.debugger.legacy.stats.LegacyParseNode;
import com.github.fge.grappa.debugger.legacy.stats.LegacyParseTreeBuilder;
import com.github.fge.grappa.debugger.legacy.stats.LegacyTraceEvent;
import com.github.fge.grappa.debugger.legacy.stats.ParsingRunTrace;
import com.github.fge.grappa.debugger.legacy.stats.RuleStatistics;
import com.github.fge.grappa.trace.TraceEventType;

import javax.annotation.Nonnull;
import javax.annotation.Untainted;
import javax.annotation.concurrent.NotThreadSafe;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@NotThreadSafe
public final class DefaultLegacyTraceTabModel
    implements LegacyTraceTabModel
{
    private static final ObjectMapper MAPPER = new ObjectMapper()
        .disable(Feature.AUTO_CLOSE_TARGET);
    private static final int BUFSIZE = 16384;
    private static final String TRACE_PATH = "/trace.json";
    private static final String INPUT_TEXT_PATH = "/input.txt";

    private final ParsingRunTrace trace;
    private final InputBuffer buffer;

    private final List<LegacyTraceEvent> traceEvents;
    private final Collection<RuleStatistics> ruleStats;
    private final LegacyParseNode rootNode;
    private final int nrEmptyMatches;

    public DefaultLegacyTraceTabModel(final FileSystem zipfs)
        throws IOException
    {
        trace = loadTrace(zipfs);
        buffer = loadBuffer(zipfs);

        final List<LegacyTraceEvent> events = trace.getEvents();
        traceEvents = relativize(events);
        ruleStats = collectStatistics(events);
        final LegacyParseTreeBuilder builder
            = new LegacyParseTreeBuilder(events);
        rootNode = builder.getRootNode();
        nrEmptyMatches = builder.getNrEmptyMatches();
    }

    @Nonnull
    @Override
    public ParsingRunTrace getTrace()
    {
        return trace;
    }

    @Nonnull
    @Override
    public InputBuffer getInputBuffer()
    {
        return buffer;
    }

    @Untainted
    @Nonnull
    @Override
    public List<LegacyTraceEvent> getTraceEvents()
    {
        return Collections.unmodifiableList(traceEvents);
    }

    @Nonnull
    @Override
    public Collection<RuleStatistics> getRuleStats()
    {
        return Collections.unmodifiableCollection(ruleStats);
    }

    @Nonnull
    @Override
    public LegacyParseNode getParseTreeRoot()
    {
        return rootNode;
    }

    @Override
    public int getNrEmptyMatches()
    {
        return nrEmptyMatches;
    }

    private InputBuffer loadBuffer(final FileSystem zipfs)
        throws IOException
    {
        final Path path = zipfs.getPath(INPUT_TEXT_PATH);
        final StringBuilder sb = new StringBuilder();
        final char[] buf = new char[BUFSIZE];
        int nrChars;

        try (
            final BufferedReader reader = Files.newBufferedReader(path);
        ) {
            while ((nrChars = reader.read(buf)) != -1)
                sb.append(buf, 0, nrChars);
        }

        return new CharSequenceInputBuffer(sb.toString());
    }

    private ParsingRunTrace loadTrace(final FileSystem zipfs)
        throws IOException
    {
        final Path tracePath = zipfs.getPath(TRACE_PATH);

        try (
            final BufferedReader reader = Files.newBufferedReader(tracePath);
        ) {
            return MAPPER.readValue(reader, ParsingRunTrace.class);
        }
    }

    private static List<LegacyTraceEvent> relativize(
        final List<LegacyTraceEvent> events)
    {
        if (events.isEmpty())
            throw new IllegalStateException("illegal trace file: "
                + "no recorded events");

        final long startTime = events.get(0).getNanoseconds();

        return events.parallelStream()
            .map(event -> timeRelative(event, startTime))
            .collect(Collectors.toList());
    }

    private static LegacyTraceEvent timeRelative(final LegacyTraceEvent orig,
        final long startTime)
    {
        return new LegacyTraceEvent(orig.getType(),
            orig.getNanoseconds() - startTime, orig.getIndex(),
            orig.getMatcher(), orig.getPath(), orig.getLevel());
    }

    private static Collection<RuleStatistics> collectStatistics(
        final Iterable<LegacyTraceEvent> events)
    {
        final Map<String, RuleStatistics> statistics = new LinkedHashMap<>();

        TraceEventType type;
        String matcher;

        for (final LegacyTraceEvent event: events) {
            type = event.getType();
            matcher = event.getMatcher();

            if (type == TraceEventType.BEFORE_MATCH) {
                statistics.computeIfAbsent(matcher, RuleStatistics::new);
                continue;
            }

            final boolean success = type == TraceEventType.MATCH_SUCCESS;
            final RuleStatistics stats = statistics.get(matcher);
            stats.addInvocation(success);
        }

        return statistics.values();
    }
}
