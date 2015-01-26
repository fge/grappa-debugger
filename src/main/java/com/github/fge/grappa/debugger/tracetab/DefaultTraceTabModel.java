package com.github.fge.grappa.debugger.tracetab;

import com.fasterxml.jackson.core.JsonGenerator.Feature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.grappa.buffers.CharSequenceInputBuffer;
import com.github.fge.grappa.buffers.InputBuffer;
import com.github.fge.grappa.debugger.stats.ParseNode;
import com.github.fge.grappa.debugger.stats.ParseTreeProcessor;
import com.github.fge.grappa.trace.ParseRunInfo;
import com.github.fge.grappa.trace.TraceEvent;

import javax.annotation.Nonnull;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

public final class DefaultTraceTabModel
    implements TraceTabModel
{
    private static final Charset UTF8 = StandardCharsets.UTF_8;
    private static final ObjectMapper MAPPER = new ObjectMapper()
        .disable(Feature.AUTO_CLOSE_TARGET);
    private static final TypeReference<List<TraceEvent>> TYPE_REF
        = new TypeReference<List<TraceEvent>>() {};

    private static final int BUFSIZE = 16384;

    private static final String INFO_PATH = "/info.json";
    private static final String TRACE_PATH = "/trace.json";
    private static final String INPUT_TEXT_PATH = "/input.txt";

    private final InputBuffer inputBuffer;
    private final ParseRunInfo info;
    private final List<TraceEvent> events;
    private final ParseNode rootNode;

    public DefaultTraceTabModel(final FileSystem zipfs)
        throws IOException
    {
        inputBuffer = loadBuffer(zipfs);
        info = loadInfo(zipfs);
        events = loadEvents(zipfs);

        final ParseTreeProcessor parseTreeProcessor = new ParseTreeProcessor();
        events.stream().forEach(parseTreeProcessor::process);
        rootNode = parseTreeProcessor.getRootNode();
    }

    @Nonnull
    @Override
    public InputBuffer getInputBuffer()
    {
        return inputBuffer;
    }

    @Nonnull
    @Override
    public ParseRunInfo getInfo()
    {
        return info;
    }

    @Nonnull
    @Override
    public List<TraceEvent> getEvents()
    {
        return Collections.unmodifiableList(events);
    }

    @Nonnull
    @Override
    public ParseNode getRootNode()
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
            final BufferedReader reader = Files.newBufferedReader(path, UTF8);
        ) {
            while ((nrChars = reader.read(buf)) != -1)
                sb.append(buf, 0, nrChars);
        }

        return new CharSequenceInputBuffer(sb);
    }

    private ParseRunInfo loadInfo(final FileSystem zipfs)
        throws IOException
    {
        final Path path = zipfs.getPath(INFO_PATH);

        try (
            final BufferedReader reader = Files.newBufferedReader(path, UTF8);
        ) {
            return MAPPER.readValue(reader, ParseRunInfo.class);
        }
    }

    private List<TraceEvent> loadEvents(final FileSystem zipfs)
        throws IOException
    {
        final Path path = zipfs.getPath(TRACE_PATH);
        final List<TraceEvent> list;

        try (
            final BufferedReader reader = Files.newBufferedReader(path, UTF8);
        ) {
            list = MAPPER.readValue(reader, TYPE_REF);
        }

        final long start = list.get(0).getNanoseconds();
        list.forEach(
            event -> event.setNanoseconds(event.getNanoseconds() - start)
        );

        return list;
    }
}
