package com.github.fge.grappa.debugger.jooq;

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

import static com.github.fge.grappa.debugger.jooq.Tables.MATCHERS;
import static com.github.fge.grappa.debugger.jooq.Tables.NODES;

public final class JooqInsertTest
{
    private static final Pattern SEMICOLON = Pattern.compile(";");

    public static void main(final String... args)
        throws SQLException, IOException
    {
        final String username = "sa";
        final String passwd = "";
        final String url = "jdbc:h2:~/tmp/testdb";

        final AtomicInteger count = new AtomicInteger(0);

        final Path path = Paths.get("/home/fge/tmp/trace2/nodes.csv");

        try (
            final Connection connection
                = DriverManager.getConnection(url, username, passwd);
            final Stream<String> lines
                = Files.lines(path, StandardCharsets.UTF_8);
        ) {
            final DSLContext db = DSL.using(connection, SQLDialect.H2);

//            lines.parallel().forEach(line -> {
//                final String[] elements = SEMICOLON.split(line, 4);
//                final int index = Integer.parseInt(elements[0]);
//                final String className = elements[1];
//                final String type = elements[2];
//                final String name = elements[3];
//
//                db.insertInto(MATCHERS,
//                    MATCHERS.ID,
//                    MATCHERS.CLASS_NAME,
//                    MATCHERS.MATCHER_TYPE,
//                    MATCHERS.NAME
//                ).values(index, className, type, name)
//                    .execute();
//            });

//            lines.parallel().peek(ignored -> {
//                final int i = count.incrementAndGet();
//                if (i % 25_000 == 0)
//                    System.out.println(i + " records processed");
//            }).forEach(line -> {
//                final String[] elements = SEMICOLON.split(line);
//                final int parentId = Integer.parseInt(elements[0]);
//                final int nodeId = Integer.parseInt(elements[1]);
//                final int level = Integer.parseInt(elements[2]);
//                final boolean success = elements[3].charAt(0) == '1';
//                final int matcherId = Integer.parseInt(elements[4]);
//                final int startIndex = Integer.parseInt(elements[5]);
//                final int endIndex = Integer.parseInt(elements[6]);
//                final long time = Long.parseLong(elements[7]);
//
//                db.insertInto(NODES,
//                    NODES.ID,
//                    NODES.PARENT_ID,
//                    NODES.LEVEL,
//                    NODES.SUCCESS,
//                    NODES.MATCHER_ID,
//                    NODES.START_INDEX,
//                    NODES.END_INDEX,
//                    NODES.TIME
//                ).values(
//                    nodeId,
//                    parentId,
//                    level,
//                    success ? 1 : 0,
//                    matcherId,
//                    startIndex,
//                    endIndex,
//                    time
//                ).execute();
//            });

//            db.select(DSL.count())
//                .from(NODES)
//                .where(NODES.SUCCESS.equal(1))
//                .and(NODES.START_INDEX.equal(NODES.END_INDEX))
//                .fetch().forEach(field -> System.out.println(field.value1()));
//
//            db.select(DSL.count())
//                .from(NODES)
//                .where(NODES.SUCCESS.equal(1))
//                .and(NODES.START_INDEX.notEqual(NODES.END_INDEX))
//                .fetch().forEach(field -> System.out.println(field.value1()));
//
//            db.select(DSL.count())
//                .from(NODES)
//                .where(NODES.SUCCESS.equal(0))
//                .fetch().forEach(field -> System.out.println(field.value1()));

//            final Field<String> status = DSL.decode()
//                .when(NODES.SUCCESS.eq(0), "fail")
//                .when(NODES.SUCCESS.eq(1).and(NODES.START_INDEX.equal(
//                        NODES.END_INDEX)), "empty")
//                .otherwise("nonempty")
//                .as("status");
//            db.select(status, DSL.count().as("count"))
//                .from(NODES)
//                .groupBy(status)
//                .fetch()
//                .forEach(record -> System.out.println(record.value1() + ": "
//                    + record.value2()));

            db.select(NODES.ID, MATCHERS.NAME)
                .from(NODES, MATCHERS)
                .where(NODES.MATCHER_ID.equal(MATCHERS.ID))
                .and(NODES.PARENT_ID.equal(0))
                .fetch()
                .forEach(r -> System.out.printf("%d: %s\n", r.value1(),
                    r.value2())
                );

            db.select(NODES.LEVEL, DSL.count()).from(NODES)
                .groupBy(NODES.LEVEL)
                .fetch()
                .forEach(r -> System.out.printf("%d: %d\n", r.value1(),
                        r.value2()));
//            final Result<Record2<Integer, String>> action = db.select(
//                MATCHERS.ID, MATCHERS.NAME).from(MATCHERS).where(
//                MATCHERS.MATCHER_TYPE.equal("ACTION")).fetch();
//
//            action.forEach(System.out::println);

        }
    }
}
