package com.github.fge.grappa.debugger.db.h2;

import com.google.common.base.Charsets;
import org.jooq.DSLContext;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import static com.github.fge.grappa.debugger.jooq.h2.Tables.MATCHERS;
import static com.github.fge.grappa.debugger.jooq.h2.Tables.NODES;

@ParametersAreNonnullByDefault
public final class H2TraceDbLoader
{
    private static final Charset UTF8 = Charsets.UTF_8;

    private static final String MATCHERS_PATH = "/matchers.csv";
    private static final String NODES_PATH = "/nodes.csv";

    private final CsvMatchersRecord csvToMatcher = new CsvMatchersRecord();

    private final CsvNodesRecord csvToNode = new CsvNodesRecord();

    private final Path matchersPath;
    private final Path nodesPath;
    private final H2TraceDbLoadStatus status = new H2TraceDbLoadStatus();

    private final DSLContext jooq;

    public H2TraceDbLoader(final FileSystem zipfs, final DSLContext jooq)
    {
        this.jooq = jooq;

        matchersPath = zipfs.getPath(MATCHERS_PATH);
        nodesPath = zipfs.getPath(NODES_PATH);
    }

    public H2TraceDbLoadStatus getStatus()
    {
        return status;
    }

    public void loadAll()
        throws IOException
    {
        try {
            insertMatchers(jooq);
            insertNodes(jooq);
        } finally {
            status.setReady();
        }
    }

    private void insertMatchers(final DSLContext jooq)
        throws IOException
    {
        try (
            final Stream<String> lines = Files.lines(matchersPath, UTF8);
        ) {
            lines.map(csvToMatcher)
                .peek(ignored -> status.incrementProcessedMatchers())
                .forEach(r -> jooq.insertInto(MATCHERS).set(r).execute());
        }
    }

    private void insertNodes(final DSLContext jooq)
        throws IOException
    {
        try (
            final Stream<String> lines = Files.lines(nodesPath, UTF8);
        ) {
            lines.map(csvToNode)
                .peek(ignored -> status.incrementProcessedNodes())
                .forEach(r -> jooq.insertInto(NODES).set(r).execute());
        }
    }
}
