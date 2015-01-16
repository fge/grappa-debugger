package com.github.parboiled1.grappa.debugger.tracetab;

import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.parboiled1.grappa.buffers.CharSequenceInputBuffer;
import com.github.parboiled1.grappa.buffers.InputBuffer;
import com.github.parboiled1.grappa.trace.ParsingRunTrace;
import com.github.parboiled1.grappa.trace.TraceEvent;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class TestTraceTabModel
    implements TraceTabModel
{
    private static final Map<String, ?> ENV
        = Collections.singletonMap("readonly", "true");
    private static final TypeReference<List<TraceEvent>> TYPE_REF
        = new TypeReference<List<TraceEvent>>() {};

    private final List<TraceEvent> events;
    private final String inputText;

    public TestTraceTabModel()
        throws IOException
    {
        final Path zip = Paths.get("/tmp/trace.zip");
        final URI uri = URI.create("jar:" + zip.toUri());

        try (
            final FileSystem zipfs = FileSystems.newFileSystem(uri, ENV);
        ) {
            inputText = readInputText(zipfs);
            events = readEvents(zipfs);
        }
    }

    private List<TraceEvent> readEvents(final FileSystem zipfs)
        throws IOException
    {
        final ObjectMapper mapper = new ObjectMapper()
            .disable(Feature.AUTO_CLOSE_SOURCE);

        final Path path = zipfs.getPath("/trace.json");
        try (
            final BufferedReader reader = Files.newBufferedReader(path);
        ) {
            final ParsingRunTrace trace
                = mapper.readValue(reader, ParsingRunTrace.class);
            return trace.getEvents();
        }
    }

    private String readInputText(final FileSystem zipfs)
        throws IOException
    {
        final Path path = zipfs.getPath("/input.txt");
        final StringBuilder sb = new StringBuilder();
        final char[] buf = new char[16384];
        int nrChars;

        try (
            final BufferedReader reader = Files.newBufferedReader(path);
        ) {
            while ((nrChars = reader.read(buf)) != -1)
                sb.append(buf, 0, nrChars);
        }

        return sb.toString();
    }

    @Override
    public List<TraceEvent> getTraceEvents()
    {
        return Collections.unmodifiableList(events);
    }

    @Override
    public InputBuffer getInputText()
    {
        return new CharSequenceInputBuffer(inputText);
    }
}
