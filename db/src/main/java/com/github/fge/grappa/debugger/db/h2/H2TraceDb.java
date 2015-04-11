package com.github.fge.grappa.debugger.db.h2;

import com.github.fge.grappa.debugger.ParseInfo;
import com.github.fge.grappa.debugger.TraceDb;
import com.github.fge.grappa.debugger.TraceDbLoadStatus;
import org.jooq.DSLContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Map;
import java.util.regex.Pattern;

public final class H2TraceDb
    implements TraceDb
{
    private static final Pattern SEMICOLON = Pattern.compile(";");
    private static final Charset UTF8 = StandardCharsets.UTF_8;

    private static final String INFO_PATH = "/info.csv";
    private static final Map<String, ?> ENV
        = Collections.singletonMap("readonly", "true");

    private final FileSystem fs;
    private final DSLContext jooq;
    private final H2TraceDbLoader loader;
    private final ParseInfo info;

    public H2TraceDb(final Path zipfile, final DSLContext jooq)
        throws IOException
    {
        final URI uri = URI.create("jar:" + zipfile.toUri());

        fs = FileSystems.newFileSystem(uri, ENV);
        this.jooq = jooq;

        loader = new H2TraceDbLoader(fs,jooq);

        final Path path = fs.getPath(INFO_PATH);

        try (
            final BufferedReader reader = Files.newBufferedReader(path, UTF8);
        ) {
            final String[] elements = SEMICOLON.split(reader.readLine());

            final long epoch = Long.parseLong(elements[0]);
            final Instant instant = Instant.ofEpochMilli(epoch);
            final ZoneId zone = ZoneId.systemDefault();
            final LocalDateTime time = LocalDateTime.ofInstant(instant, zone);

            final int treeDepth = Integer.parseInt(elements[1]);
            final int nrMatchers = Integer.parseInt(elements[2]);
            final int nrLines = Integer.parseInt(elements[3]);
            final int nrChars = Integer.parseInt(elements[4]);
            final int nrCodePoints = Integer.parseInt(elements[5]);
            final int nrInvocations = Integer.parseInt(elements[6]);

            info = new ParseInfo(time, treeDepth, nrMatchers, nrLines, nrChars,
                nrCodePoints, nrInvocations);
        }
    }

    @Override
    public TraceDbLoadStatus getLoadStatus()
    {
        return loader.getStatus();
    }

    @Override
    public ParseInfo getParseInfo()
    {
        return info;
    }

    @Override
    public DSLContext getJooq()
    {
        return jooq;
    }
}
