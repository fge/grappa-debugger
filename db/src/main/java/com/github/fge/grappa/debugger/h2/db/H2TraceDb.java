package com.github.fge.grappa.debugger.h2.db;

import com.github.fge.filesystem.MoreFiles;
import com.github.fge.filesystem.RecursionMode;
import com.github.fge.grappa.buffers.CharSequenceInputBuffer;
import com.github.fge.grappa.buffers.InputBuffer;
import com.github.fge.grappa.debugger.ParseInfo;
import com.github.fge.grappa.debugger.TraceDb;
import com.github.fge.grappa.debugger.TraceDbLoadStatus;
import com.github.fge.grappa.debugger.h2.db.load.H2TraceDbLoader;
import com.github.fge.grappa.debugger.h2.model.H2TraceModel;
import com.github.fge.grappa.debugger.model.TraceModel;
import com.github.fge.lambdas.Throwing;
import com.google.common.io.CharStreams;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.h2.jdbcx.JdbcConnectionPool;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;

public final class H2TraceDb
    implements TraceDb
{
    private static final Pattern SEMICOLON = Pattern.compile(";");
    private static final Charset UTF8 = StandardCharsets.UTF_8;

    private static final Map<String, ?> ENV
        = Collections.singletonMap("readonly", "true");

    private static final String INFO_PATH = "/info.csv";
    private static final String INPUT_TEXT_PATH = "/input.txt";


    private final FileSystem fs;
    private final Path dbpath;
    private final DSLContext jooq;
    private final JdbcConnectionPool pool;

    private final H2TraceDbLoader loader;
    private final AtomicReference<Throwable> loadError
        = new AtomicReference<>();
    private final ThreadFactory threadFactory
        = new ThreadFactoryBuilder()
        .setDaemon(true)
        .setNameFormat("grappa-db-load-%d")
        .setUncaughtExceptionHandler((t, e) -> loadError.set(e))
        .build();
    private final ExecutorService executor
        = Executors.newSingleThreadExecutor(threadFactory);

    private final ParseInfo info;
    private final InputBuffer inputBuffer;

    public H2TraceDb(final Path zipfile, final Path dbpath,
        final DSLContext jooq, final JdbcConnectionPool pool)
        throws IOException
    {
        final URI uri = URI.create("jar:" + zipfile.toUri());

        fs = FileSystems.newFileSystem(uri, ENV);
        this.dbpath = dbpath;
        this.jooq = jooq;
        this.pool = pool;

        loader = new H2TraceDbLoader(fs, jooq, loadError);
        executor.submit(Throwing.runnable(loader::loadAll));

        info = loadParseInfo();
        inputBuffer = loadInputBuffer();
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
    public InputBuffer getInputBuffer()
    {
        return inputBuffer;
    }

    @Override
    public TraceModel getModel()
    {
        return new H2TraceModel(jooq, inputBuffer);
    }

    private ParseInfo loadParseInfo()
        throws IOException
    {
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

            return new ParseInfo(time, treeDepth, nrMatchers, nrLines, nrChars,
                nrCodePoints, nrInvocations);
        }
    }

    private InputBuffer loadInputBuffer()
        throws IOException
    {
        final Path path = fs.getPath(INPUT_TEXT_PATH);

        try (
            final BufferedReader reader = Files.newBufferedReader(path, UTF8);
        ) {
            final StringBuilder sb = new StringBuilder();
            CharStreams.copy(reader, sb);

            return new CharSequenceInputBuffer(sb);
        }

    }

    @Override
    public void close()
        throws IOException
    {
        IOException exception = null;

        executor.shutdownNow();
        pool.dispose();

        try {
            fs.close();
        } catch (IOException e) {
            exception = e;
        }

        try {
            MoreFiles.deleteRecursive(dbpath, RecursionMode.KEEP_GOING);
        } catch (IOException e) {
            if (exception == null)
                exception = e;
            else
                exception.addSuppressed(e);
        }

        if (exception != null)
            throw exception;
    }
}
