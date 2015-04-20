package com.github.fge.grappa.debugger.zip2db;

import com.github.fge.filesystem.MoreFileSystems;
import com.github.fge.filesystem.MoreFiles;
import com.github.fge.filesystem.RecursionMode;
import com.github.fge.grappa.debugger.postgresql.db.PostgresqlTraceDbFactory;
import com.github.fge.grappa.debugger.postgresql.jooq.tables.records.ParseInfoRecord;
import com.github.fge.lambdas.Throwing;
import com.github.fge.lambdas.runnable.ThrowingRunnable;
import com.google.common.base.Stopwatch;
import com.google.common.io.CharStreams;
import org.jooq.DSLContext;
import org.jooq.Field;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static com.github.fge.grappa.debugger.postgresql.jooq.Tables.MATCHERS;
import static com.github.fge.grappa.debugger.postgresql.jooq.Tables.NODES;
import static com.github.fge.grappa.debugger.postgresql.jooq.Tables.PARSE_INFO;

public final class Zip2Db2
{
    private static final Pattern SEMICOLON = Pattern.compile(";");
    private static final Function<String, String> CSV_ESCAPE
        = TraceCsvEscaper.ESCAPER::apply;

    private static final String INFO_PATH = "/info.csv";
    private static final String INPUT_PATH = "/input.txt";
    private static final String MATCHERS_PATH = "/matchers.csv";
    private static final String NODES_PATH = "/nodes.csv";

    private static final List<Field<?>> MATCHERS_FIELDS = Arrays.asList(
        MATCHERS.PARSE_INFO_ID, MATCHERS.ID, MATCHERS.CLASS_NAME,
        MATCHERS.MATCHER_TYPE, MATCHERS.NAME
    );

    private static final List<Field<?>> NODES_FIELDS = Arrays.asList(
        NODES.PARSE_INFO_ID, NODES.PARENT_ID, NODES.ID, NODES.LEVEL,
        NODES.SUCCESS, NODES.MATCHER_ID, NODES.START_INDEX, NODES.END_INDEX,
        NODES.TIME
    );

    private final FileSystem fs;
    private final DSLContext jooq;
    private final UUID uuid;

    private final Path tmpdir;

    public Zip2Db2(final FileSystem fs, final DSLContext jooq, final UUID uuid)
        throws IOException
    {
        this.fs = fs;
        this.jooq = jooq;
        this.uuid = uuid;

        tmpdir = Files.createTempDirectory("zip2db");
    }

    public void removeTmpdir()
        throws IOException
    {
        MoreFiles.deleteRecursive(tmpdir, RecursionMode.KEEP_GOING);
    }

    public void run()
    {
        time(this::generateMatchersCsv, "Generate matchers CSV");
        time(this::generateNodesCsv, "Generate nodes CSV");
        time(this::writeInfo, "Write info record");
        time(this::writeMatchers, "Write matchers");
        time(this::writeNodes, "Write nodes");
    }

    private void generateMatchersCsv()
        throws IOException
    {
        final Path src = fs.getPath(MATCHERS_PATH);
        final Path dst = tmpdir.resolve("matchers.csv");

        try (
            final Stream<String> lines = Files.lines(src);
            final BufferedWriter writer = Files.newBufferedWriter(dst,
                StandardOpenOption.CREATE_NEW);
        ) {
            lines.map(this::toMatchersLine)
                .forEach(Throwing.consumer(writer::write));
        }
    }

    private String toMatchersLine(final String input)
    {
        final List<String> parts = new ArrayList<>();
        parts.add('"' + uuid.toString() + '"');
        Arrays.stream(SEMICOLON.split(input, 4))
            .map(s -> '"' + CSV_ESCAPE.apply(s) + '"')
            .forEach(parts::add);
        return String.join(";", parts) + '\n';
    }

    private void generateNodesCsv()
        throws IOException
    {
        final Path src = fs.getPath(NODES_PATH);
        final Path dst = tmpdir.resolve("nodes.csv");

        try (
            final Stream<String> lines = Files.lines(src);
            final BufferedWriter writer = Files.newBufferedWriter(dst,
                StandardOpenOption.CREATE_NEW);
        ) {
            lines.map(this::toNodesLine)
                .forEach(Throwing.consumer(writer::write));
        }
    }

    private String toNodesLine(final String input)
    {
        final List<String> parts = new ArrayList<>();
        parts.add('"' + uuid.toString() + '"');
        SEMICOLON.splitAsStream(input)
            .map(s -> '"' + CSV_ESCAPE.apply(s) + '"')
            .forEach(parts::add);
        return String.join(";", parts) + '\n';
    }

    private void writeInfo()
        throws IOException
    {
        final Path path = fs.getPath(INFO_PATH);

        try (
            final BufferedReader reader = Files.newBufferedReader(path);
        ) {
            final String[] elements = SEMICOLON.split(reader.readLine());

            final long epoch = Long.parseLong(elements[0]);
            final Instant instant = Instant.ofEpochMilli(epoch);
            final ZoneId zone = ZoneId.systemDefault();
            final LocalDateTime time = LocalDateTime.ofInstant(instant, zone);

            final ParseInfoRecord record = jooq.newRecord(PARSE_INFO);

            record.setId(uuid);
            record.setContent(loadText());
            record.setDate(Timestamp.valueOf(time));

            record.insert();
        }
    }

    private String loadText()
        throws IOException
    {
        final Path path = fs.getPath(INPUT_PATH);

        try (
            final BufferedReader reader = Files.newBufferedReader(path);
        ) {
            return CharStreams.toString(reader);
        }
    }

    private void writeMatchers()
        throws IOException
    {
        final Path path = tmpdir.resolve("matchers.csv");

        try (
            final BufferedReader reader = Files.newBufferedReader(path);
        ) {
            jooq.loadInto(MATCHERS)
                .onErrorAbort()
                .loadCSV(reader)
                .fields(MATCHERS_FIELDS)
                .separator(';')
                .execute();
        }
    }

    private void writeNodes()
        throws IOException
    {
        final Path path = tmpdir.resolve("nodes.csv");

        try (
            final BufferedReader reader = Files.newBufferedReader(path);
        ) {
            jooq.loadInto(NODES)
                .onErrorAbort()
                .loadCSV(reader)
                .fields(NODES_FIELDS)
                .separator(';')
                .execute();
        }
    }

    private void time(final ThrowingRunnable runnable, final String description)
    {
        System.out.println(description + ": start");
        final Stopwatch stopwatch = Stopwatch.createStarted();
        runnable.run();
        System.out.println(description + ": done (" + stopwatch.stop() + ')');
    }

    public static void main(final String... args)
        throws IOException
    {
        if (args.length != 1) {
            System.err.println("missing zip argument");
            System.exit(2);
        }

        final Path zip = Paths.get(args[0]).toRealPath();

        final UUID uuid = UUID.randomUUID();
        final DSLContext jooq = PostgresqlTraceDbFactory.defaultFactory()
            .getJooq();

        try (
            final FileSystem fs = MoreFileSystems.openZip(zip, true);
        ) {
            final Zip2Db2 zip2Db = new Zip2Db2(fs, jooq, uuid);
            try {
                zip2Db.run();
            } finally {
                zip2Db.removeTmpdir();
            }
        }
    }
}
