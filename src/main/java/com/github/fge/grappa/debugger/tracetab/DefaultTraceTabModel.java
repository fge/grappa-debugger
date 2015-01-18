package com.github.fge.grappa.debugger.tracetab;

import com.fasterxml.jackson.core.JsonGenerator.Feature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.grappa.debugger.tracetab.statistics.InputTextInfo;
import com.github.fge.grappa.debugger.tracetab.statistics.ParseNode;
import com.github.fge.grappa.debugger.tracetab.statistics.ParseTreeBuilder;
import com.github.fge.grappa.debugger.tracetab.statistics.RuleStatistics;
import com.github.parboiled1.grappa.buffers.CharSequenceInputBuffer;
import com.github.parboiled1.grappa.buffers.InputBuffer;
import com.github.parboiled1.grappa.trace.ParsingRunTrace;
import com.github.parboiled1.grappa.trace.TraceEvent;
import com.github.parboiled1.grappa.trace.TraceEventType;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@NotThreadSafe
public final class DefaultTraceTabModel
    implements TraceTabModel
{
    private static final Map<String, ?> ENV
        = Collections.singletonMap("readonly", "true");
    private static final ObjectMapper MAPPER = new ObjectMapper()
        .disable(Feature.AUTO_CLOSE_TARGET);
    private static final int BUFSIZE = 16384;
    private static final String TRACE_PATH = "/trace.json";
    private static final String INPUT_TEXT_PATH = "/input.txt";

    private final ParsingRunTrace trace;
    private final InputBuffer buffer;

    private final List<TraceEvent> traceEvents;
    private final InputTextInfo textInfo;
    private final Collection<RuleStatistics> ruleStats;
    private final ParseNode rootNode;

    public DefaultTraceTabModel(final Path zipPath)
        throws IOException
    {
        final URI uri = URI.create("jar:" + zipPath.toUri());

        try (
            final FileSystem zipfs = FileSystems.newFileSystem(uri, ENV);
        ) {
            trace = loadTrace(zipfs);
            buffer = loadBuffer(zipfs);
        }

        final List<TraceEvent> events = trace.getEvents();
        traceEvents = relativize(events);
        textInfo = new InputTextInfo(buffer);
        ruleStats = collectStatistics(events);
        rootNode = new ParseTreeBuilder(traceEvents).getRootNode();
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

    @Nonnull
    @Override
    public List<TraceEvent> getTraceEvents()
    {
        return Collections.unmodifiableList(traceEvents);
    }

    @Nonnull
    @Override
    public InputTextInfo getInputTextInfo()
    {
        return textInfo;
    }

    @Nonnull
    @Override
    public Collection<RuleStatistics> getRuleStats()
    {
        return Collections.unmodifiableCollection(ruleStats);
    }

    @Nonnull
    @Override
    public ParseNode getParseTreeRoot()
    {
        return rootNode;
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

        return new CharSequenceInputBuffer(sb);
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

    private static List<TraceEvent> relativize(final List<TraceEvent> events)
    {
        if (events.isEmpty())
            return Collections.emptyList();

        final long startTime = events.get(0).getNanoseconds();

        return events.parallelStream()
            .map(event -> timeRelative(event, startTime))
            .collect(Collectors.toList());
    }

    private static TraceEvent timeRelative(final TraceEvent orig,
        final long startTime)
    {
        return new TraceEvent(orig.getType(), orig.getNanoseconds() - startTime,
            orig.getIndex(), orig.getMatcher(), orig.getPath(),
            orig.getLevel());
    }

    private static Collection<RuleStatistics> collectStatistics(
        final Iterable<TraceEvent> events)
    {
        final Deque<TraceEvent> eventStack = new ArrayDeque<>();
        final Map<String, RuleStatistics> statistics = new LinkedHashMap<>();

        TraceEventType type;
        long nanos;
        String matcher;

        for (final TraceEvent event: events) {
            type = event.getType();
            nanos = event.getNanoseconds();
            matcher = event.getMatcher();

            if (type == TraceEventType.BEFORE_MATCH) {
                eventStack.push(event);
                statistics.computeIfAbsent(matcher, RuleStatistics::new);
                continue;
            }

            final boolean success = type == TraceEventType.MATCH_SUCCESS;
            final TraceEvent startEvent = eventStack.pop();
            final RuleStatistics stats = statistics.get(matcher);
            stats.addInvocation(nanos - startEvent.getNanoseconds(), success);
        }

        return statistics.values();
    }
}
