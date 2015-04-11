package com.github.fge.grappa.debugger.db.h2;

import org.h2.jdbcx.JdbcConnectionPool;
import org.jooq.ConnectionProvider;
import org.jooq.exception.DataAccessException;

import java.sql.Connection;
import java.sql.SQLException;

public final class H2ConnectionProvider
    implements ConnectionProvider
{
    private final JdbcConnectionPool pool;

    public H2ConnectionProvider(final JdbcConnectionPool pool)
    {
        this.pool = pool;
    }

    @Override
    public Connection acquire()
    {
        try {
            return pool.getConnection();
        } catch (SQLException e) {
            throw new DataAccessException("cannot acquire connection", e);

        }
    }

    @Override
    public void release(final Connection connection)
    {
        try {
            connection.close();
        } catch (SQLException e) {
            throw new DataAccessException("failed to close connection", e);
        }
    }
}
