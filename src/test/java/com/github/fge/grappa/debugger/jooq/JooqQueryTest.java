package com.github.fge.grappa.debugger.jooq;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static com.github.fge.grappa.debugger.jooq.Tables.MATCHERS;
import static com.github.fge.grappa.debugger.jooq.Tables.NODES;

public final class JooqQueryTest
{
    public static void main(final String... args)
        throws SQLException
    {
        final String username = "sa";
        final String passwd = "";
        final String url = "jdbc:h2:~/tmp/testdb2";

        try (
            final Connection connection
                = DriverManager.getConnection(url, username, passwd);
        ) {
            final DSLContext db = DSL.using(connection, SQLDialect.H2);

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
