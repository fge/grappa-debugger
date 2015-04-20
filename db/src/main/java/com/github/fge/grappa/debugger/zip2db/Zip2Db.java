package com.github.fge.grappa.debugger.zip2db;

import com.github.fge.filesystem.MoreFileSystems;
import com.github.fge.grappa.debugger.postgresql.db.PostgresqlTraceDbFactory;
import com.github.fge.grappa.debugger.postgresql.jooq.tables.records.ParseInfoRecord;

import com.google.common.base.Stopwatch;
import com.google.common.io.CharStreams;
import org.jooq.DSLContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static com.github.fge.grappa.debugger.postgresql.jooq.Tables.PARSE_INFO;

public final class Zip2Db
{
    private static final Pattern SEMICOLON = Pattern.compile(";");

    private static final String INFO_PATH = "/info.csv";
    private static final String INPUT_PATH = "/input.txt";
    private static final String MATCHERS_PATH = "/matchers.csv";
    private static final String NODES_PATH = "/nodes.csv";

    private final FileSystem fs;
    private final DSLContext jooq;
    private final UUID uuid;

    private final CsvMatchersRecord matchersRecord;
    private final CsvNodesRecord nodesRecord;

    private final AtomicInteger matchersCount = new AtomicInteger(0);
    private final AtomicInteger nodesCount = new AtomicInteger(0);

    public Zip2Db(final FileSystem fs, final DSLContext jooq, final UUID uuid)
    {
        this.fs = fs;
        this.jooq = jooq;
        this.uuid = uuid;

        matchersRecord = new CsvMatchersRecord(uuid);
        nodesRecord = new CsvNodesRecord(uuid);
    }

    public void run()
        throws IOException
    {
        writeInfo();
        writeMatchers();
        writeNodes();
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
        final Path path = fs.getPath(MATCHERS_PATH);

        try (
            final Stream<String> lines = Files.lines(path).parallel();
        ) {
            final Stopwatch stopwatch = Stopwatch.createStarted();
            lines.map(matchersRecord)
                .peek(ignored -> {
                    final int count = matchersCount.incrementAndGet();
                    if (count % 25_000 == 0)
                        System.out.println("Matchers: " + count
                            + " records inserted");
                })
                .forEach(jooq::executeInsert);
            System.out.println("Matchers: finished (" + matchersCount.get()
                + ") records");
            System.out.println(stopwatch.stop());
        }
    }

    private void writeNodes()
        throws IOException
    {
        final Path path = fs.getPath(NODES_PATH);

        try (
            final Stream<String> lines = Files.lines(path).parallel();
        ) {
            final Stopwatch stopwatch = Stopwatch.createStarted();
            lines.map(nodesRecord)
                .peek(ignored -> {
                    final int count = nodesCount.incrementAndGet();
                    if (count % 25_000 == 0)
                        System.out.println("Matchers: " + count
                            + " records inserted");
                })
                .forEach(jooq::executeInsert);
            System.out.println(
                "Nodes: finished (" + nodesCount.get() + ") records");
            System.out.println(stopwatch.stop());
        }
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
            final Zip2Db zip2Db = new Zip2Db(fs, jooq, uuid);
            zip2Db.run();
        }
    }
}
