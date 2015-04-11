package com.github.fge.grappa.debugger.db.h2;

import com.github.fge.grappa.debugger.ParseInfo;
import com.github.fge.grappa.debugger.TraceDb;
import com.github.fge.grappa.debugger.TraceDbLoadStatus;
import org.jooq.DSLContext;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;

public final class H2TraceDb
    implements TraceDb
{
    private static final Map<String, ?> ENV
        = Collections.singletonMap("readonly", "true");

    private final FileSystem fs;
    private final DSLContext jooq;
    private final H2TraceDbLoader loader;

    public H2TraceDb(final Path zipfile, final DSLContext jooq)
        throws IOException
    {
        final URI uri = URI.create("jar:" + zipfile.toUri());

        fs = FileSystems.newFileSystem(uri, ENV);
        this.jooq = jooq;

        loader = new H2TraceDbLoader(fs,jooq);
    }

    @Override
    public TraceDbLoadStatus getLoadStatus()
    {
        return loader.getStatus();
    }

    @Override
    public ParseInfo getParseInfo()
    {
        // TODO
        return null;
    }

    @Override
    public DSLContext getJooq()
    {
        return jooq;
    }
}
