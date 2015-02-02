package com.github.fge.grappa.debugger.db;

import com.google.common.base.Stopwatch;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static com.github.fge.grappa.debugger.db.Tables.MATCHERS;
import static com.github.fge.grappa.debugger.db.Tables.NODES;

public final class JooqTest
{
    private static final Pattern SEMICOLON = Pattern.compile(";");

    public static void main(final String... args)
        throws SQLException, IOException
    {
        final String username = "sa";
        final String passwd = "";
        final String url = "jdbc:h2:~/tmp/testdb2;LOG=0;LOCK_MODE=0;"
            + "UNDO_LOG=0;CACHE_SIZE=131072";

        try (
            final Connection connection
                = DriverManager.getConnection(url, username, passwd)
        ) {
            final DSLContext jooq = DSL.using(connection, SQLDialect.H2);

            final Stopwatch stopwatch = Stopwatch.createStarted();
            preInsert(jooq);
            System.out.println("pre done: " + stopwatch);

            insertMatchers(jooq);
            System.out.println("matchers done: " + stopwatch);

            insertNodes(jooq);
            System.out.println("nodes done: " + stopwatch);

            postInsert(jooq);
            System.out.println("everything done: " + stopwatch);
        }
    }

    private static void preInsert(final DSLContext jooq)
    {
        final String ddlCreateMatchers = "create table matchers ("
            + "id integer not null,"
            + "class_name varchar(255) not null,"
            + "matcher_type varchar(30) not null,"
            + "name varchar(1024) not null"
            + ");";

        final String ddlCreateNodes = "create table nodes ("
            + "id integer not null,"
            + "parent_id integer not null,"
            + "level integer not null,"
            + "success integer not null,"
            + "matcher_id integer not null,"
            + "start_index integer not null,"
            + "end_index integer not null,"
            + "time long not null"
            + ");";

        jooq.execute(ddlCreateMatchers);
        jooq.execute(ddlCreateNodes);
    }

    private static void insertMatchers(final DSLContext jooq)
        throws IOException
    {
        final Path path = Paths.get("/home/fge/tmp/trace2/matchers.csv");

        try (
            final Stream<String> lines = Files.lines(path,
                StandardCharsets.UTF_8);
        ) {
            lines.forEach(line -> {
                final String[] elements = SEMICOLON.split(line, 4);
                final int index = Integer.parseInt(elements[0]);
                final String className = elements[1];
                final String type = elements[2];
                final String name = elements[3];

                jooq.insertInto(MATCHERS, MATCHERS.ID, MATCHERS.CLASS_NAME,
                    MATCHERS.MATCHER_TYPE, MATCHERS.NAME).values(index,
                    className, type, name).execute();
            });
        }

    }

    private static void insertNodes(final DSLContext jooq)
        throws IOException
    {
        final Path path = Paths.get("/home/fge/tmp/trace2/nodes.csv");

        final AtomicInteger count = new AtomicInteger(0);

        try (
            final Stream<String> lines = Files.lines(path,
                StandardCharsets.UTF_8);
        ) {
            lines.parallel().peek(ignored -> {
                final int i = count.incrementAndGet();
                if (i % 25_000 == 0)
                    System.out.println(i + " records processed");
            }).forEach(line -> {
                final String[] elements = SEMICOLON.split(line);
                final int parentId = Integer.parseInt(elements[0]);
                final int nodeId = Integer.parseInt(elements[1]);
                final int level = Integer.parseInt(elements[2]);
                final boolean success = elements[3].charAt(0) == '1';
                final int matcherId = Integer.parseInt(elements[4]);
                final int startIndex = Integer.parseInt(elements[5]);
                final int endIndex = Integer.parseInt(elements[6]);
                final long time = Long.parseLong(elements[7]);

                jooq.insertInto(NODES,
                    NODES.ID,
                    NODES.PARENT_ID,
                    NODES.LEVEL,
                    NODES.SUCCESS,
                    NODES.MATCHER_ID,
                    NODES.START_INDEX,
                    NODES.END_INDEX,
                    NODES.TIME
                ).values(
                    nodeId,
                    parentId,
                    level,
                    success ? 1 : 0,
                    matcherId,
                    startIndex,
                    endIndex,
                    time
                ).execute();
            });
        }
    }

    private static void postInsert(final DSLContext jooq)
    {
        final String ddlMatcherPkConstraint
            = "alter table matchers add primary key(id);";
        final String ddlNodesPkConstraint
            = "alter table nodes add primary key(id);";
        final String ddlNodesFkConstraint
            = "alter table nodes add foreign key (matcher_id)"
            + "references matchers(id)";

        jooq.execute(ddlMatcherPkConstraint);
        jooq.execute(ddlNodesPkConstraint);
        jooq.execute(ddlNodesFkConstraint);
    }
}
