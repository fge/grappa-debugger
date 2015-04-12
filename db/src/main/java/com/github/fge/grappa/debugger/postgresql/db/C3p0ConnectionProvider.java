package com.github.fge.grappa.debugger.postgresql.db;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.jooq.ConnectionProvider;
import org.jooq.exception.DataAccessException;

import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

@ParametersAreNonnullByDefault
public final class C3p0ConnectionProvider
    implements ConnectionProvider
{
    private final ComboPooledDataSource source;

    public C3p0ConnectionProvider(final ComboPooledDataSource source)
    {
        this.source = Objects.requireNonNull(source);
    }

    @Override
    public Connection acquire()
    {
        try {
            return source.getConnection();
        } catch (SQLException e) {
            throw new DataAccessException("cannot acquire connection", e);
        }
    }

    @Override
    public void release(final Connection connection)
    {
        // Nothing
    }
}
