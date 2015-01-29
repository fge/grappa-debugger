package com.github.fge.grappa.debugger.csvtrace;

import com.github.fge.grappa.debugger.stats.ParseNode;
import com.github.fge.grappa.debugger.stats.ParseTreeProcessor;
import com.github.fge.grappa.debugger.stats.csv.TraceEventLoaderFactory;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;

public final class DefaultCsvTraceModel
    implements CsvTraceModel
{
    private static final Map<String, ?> ZIPFS_ENV
        = Collections.singletonMap("readonly", "true");

    private final FileSystem zipfs;
    private final Path traceFile;
    private final Path inputText;

    private final TraceEventLoaderFactory loaderFactory;

    public DefaultCsvTraceModel(final Path zipPath)
        throws IOException
    {
        final URI uri = URI.create("jar:" + zipPath.toUri());
        zipfs = FileSystems.newFileSystem(uri, ZIPFS_ENV);
        traceFile = zipfs.getPath("/trace.csv");
        inputText = zipfs.getPath("/input.txt");
        loaderFactory = new TraceEventLoaderFactory(traceFile);
    }

    @Override
    public ParseNode getRootNode()
        throws IOException
    {
        final ParseTreeProcessor processor = new ParseTreeProcessor();
        loaderFactory.fullLoader().forEach(processor::process);
        return processor.getRootNode();
    }
}
