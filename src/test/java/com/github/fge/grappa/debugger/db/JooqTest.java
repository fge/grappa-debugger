package com.github.fge.grappa.debugger.db;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class JooqTest
{
    public static void main(final String... args)
        throws SQLException
    {
        final String username = "sa";
        final String passwd = "";
        final String url = "jdbc:h2:~/tmp/testdb";

        try (
            final Connection connection
                = DriverManager.getConnection(url, username, passwd)
        ) {
            final DSLContext create = DSL.using(connection, SQLDialect.H2);

            final String ddlCreateMatchers = "create table matchers ("
                + "id integer not null primary key,"
                + "class_name varchar(255) not null,"
                + "matcher_type varchar(30) not null,"
                + "name varchar(1024) not null"
                + ");";

            final String ddlCreateNodes = "create table nodes ("
                + "id integer not null primary key,"
                + "parent_id integer not null,"
                + "level integer not null,"
                + "success integer not null,"
                + "matcher_id integer not null references matchers(id),"
                + "start_index integer not null,"
                + "end_index integer not null,"
                + "time long not null"
                + ");";

            create.execute(ddlCreateMatchers);
            create.execute(ddlCreateNodes);
        }
    }
}
