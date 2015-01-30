package com.github.fge.grappa.debugger.csvtrace;

import com.fasterxml.jackson.core.JsonGenerator.Feature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.grappa.buffers.CharSequenceInputBuffer;
import com.github.fge.grappa.buffers.InputBuffer;
import com.github.fge.grappa.debugger.csvtrace.model.TraceEventSpliterator;
import com.github.fge.grappa.debugger.stats.ParseNode;
import com.github.fge.grappa.debugger.stats.ParseTreeProcessor;
import com.github.fge.grappa.trace.TraceEvent;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Spliterator;
import java.util.stream.StreamSupport;

@ParametersAreNonnullByDefault
public final class DefaultCsvTraceModel
    implements CsvTraceModel
{
    private static final int BUFSIZE = 16384;
    private static final ObjectMapper MAPPER = new ObjectMapper()
        .disable(Feature.AUTO_CLOSE_TARGET)
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private static final Charset UTF8 = StandardCharsets.UTF_8;
    private static final String INFO_PATH = "/info.json";
    private static final String INPUT_TEXT_PATH = "/input.txt";
    private static final String CSV_PATH = "/trace.csv";

    private final FileSystem zipfs;

    public DefaultCsvTraceModel(final FileSystem zipfs)
    {
        this.zipfs = Objects.requireNonNull(zipfs);
    }

    @Override
    public ParseNode getRootNode()
        throws IOException
    {
        final Path path = zipfs.getPath(CSV_PATH);
        final ParseTreeProcessor processor = new ParseTreeProcessor();

        try (
            final BufferedReader reader = Files.newBufferedReader(path, UTF8);
        ) {
            final Spliterator<TraceEvent> spliterator
                = new TraceEventSpliterator(reader);
            StreamSupport.stream(spliterator, false)
                .forEach(processor::process);
            return processor.getRootNode();
        }
    }

    @Override
    public InputBuffer getInputBuffer()
        throws IOException
    {
        final Path path = zipfs.getPath(INPUT_TEXT_PATH);

        try (
            final BufferedReader reader = Files.newBufferedReader(path, UTF8);
        ) {
            final char[] buf = new char[BUFSIZE];
            final StringBuilder sb = new StringBuilder();
            int nrChars;

            while ((nrChars = reader.read(buf)) != -1)
                sb.append(buf, 0, nrChars);

            return new CharSequenceInputBuffer(sb);
        }
    }

    @Override
    public void dispose()
        throws IOException
    {
        zipfs.close();
    }
}
