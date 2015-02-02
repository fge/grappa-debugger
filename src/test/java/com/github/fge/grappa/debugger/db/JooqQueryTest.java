package com.github.fge.grappa.debugger.db;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static com.github.fge.grappa.debugger.db.Tables.MATCHERS;
import static com.github.fge.grappa.debugger.db.Tables.NODES;

public final class JooqQueryTest
{
    public static void main(final String... args)
        throws SQLException, IOException
    {
        final String username = "sa";
        final String passwd = "";
        final String url = "jdbc:h2:~/tmp/testdb2";

        try (
            final Connection connection
                = DriverManager.getConnection(url, username, passwd);
        ) {
            final DSLContext db = DSL.using(connection, SQLDialect.H2);

//            db.select(NODES.ID, MATCHERS.NAME)
//                .from(NODES, MATCHERS)
//                .where(NODES.MATCHER_ID.equal(MATCHERS.ID))
//                .and(NODES.PARENT_ID.equal(0))
//                .fetch()
//                .forEach(r -> System.out.printf("%d: %s\n", r.value1(),
//                    r.value2())
//                );
//
//            db.select(NODES.LEVEL, DSL.count()).from(NODES)
//                .groupBy(NODES.LEVEL)
//                .fetch()
//                .forEach(r -> System.out.printf("%d: %d\n", r.value1(),
//                        r.value2()));

            db.select(MATCHERS.MATCHER_TYPE, DSL.count())
                .from(NODES, MATCHERS)
                .where(NODES.MATCHER_ID.equal(MATCHERS.ID))
                .groupBy(MATCHERS.MATCHER_TYPE)
                .fetch()
                .forEach(r -> System.out.printf("%s: %d\n", r.value1(),
                        r.value2())
                );
        }
    }
}
