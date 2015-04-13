package com.github.fge.grappa.debugger.postgresql.db;

import com.github.fge.grappa.debugger.RdbmsTraceDbFactory;
import com.github.fge.grappa.debugger.TraceDb;
import com.github.fge.grappa.debugger.model.TraceModelException;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.jooq.Configuration;
import org.jooq.ConnectionProvider;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;

import javax.annotation.ParametersAreNonnullByDefault;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Properties;
import java.util.UUID;

@ParametersAreNonnullByDefault
public final class PostgresqlTraceDbFactory
    implements RdbmsTraceDbFactory
{
    private static final String POSTGRESQL_JDBC_URL_FORMAT
        = "jdbc:postgresql://%s:%s/%s";

    private final String jdbcUrl;
    private final String user;
    private final String password;
    private final DSLContext jooq;
    private final Configuration cfg;

    public static PostgresqlTraceDbFactory defaultFactory()
    {
        return new Builder().build();
    }

    @Override
    public TraceDb create(final UUID arg)
    {
        return new PostgresqlTraceDb(jooq, arg);
    }

    private PostgresqlTraceDbFactory(final Builder builder)
    {
        jdbcUrl = String.format(POSTGRESQL_JDBC_URL_FORMAT,
            builder.host, builder.port, builder.db);
        user = builder.user;
        password = builder.password;

        final ComboPooledDataSource source = new ComboPooledDataSource();

        try {
            source.setDriverClass("org.postgresql.Driver");
        } catch (PropertyVetoException e) {
            throw new TraceModelException(e);
        }

        source.setJdbcUrl(jdbcUrl);
        source.setUser(builder.user);
        source.setPassword(builder.password);

        final ConnectionProvider provider = new C3p0ConnectionProvider(source);

        cfg = new DefaultConfiguration();
        cfg.set(provider);
        cfg.set(SQLDialect.POSTGRES);

        jooq = DSL.using(cfg);
    }

    public String getJdbcUrl()
    {
        return jdbcUrl;
    }

    public String getUser()
    {
        return user;
    }

    public String getPassword()
    {
        return password;
    }

    public Configuration getCfg()
    {
        return cfg;
    }

    public DSLContext getJooq()
    {
        return jooq;
    }

    public static final class Builder
    {
        private static final String DEFAULT_CFG_FILE
            = "/db/postgresql/postgresql_defaults.conf";
        private static final String HOST_PROPERTY = "grappa.postgresql.host";
        private static final String PORT_PROPERTY = "grappa.postgresql.port";
        private static final String DB_PROPERTY = "grappa.postgresql.database";
        private static final String USER_PROPERTY = "grappa.postgresql.user";
        private static final String PASSWORD_PROPERTY
            = "grappa.postgresql.password";

        private static final String DEFAULT_HOST;
        private static final String DEFAULT_PORT;
        private static final String DEFAULT_DB;
        private static final String DEFAULT_USER;
        private static final String DEFAULT_PASSWORD;

        static {
            final InputStream in
                = Builder.class.getResourceAsStream(DEFAULT_CFG_FILE);

            if (in == null)
                throw new ExceptionInInitializerError(DEFAULT_CFG_FILE
                    + " not found in classpath");

            final Properties properties = new Properties();
            final CharsetDecoder decoder = StandardCharsets.UTF_8
                .newDecoder()
                .onMalformedInput(CodingErrorAction.REPORT)
                .onUnmappableCharacter(CodingErrorAction.REPORT);

            try (
                final Reader reader = new InputStreamReader(in, decoder);
            ) {
                properties.load(reader);
            } catch (IOException e) {
                throw new ExceptionInInitializerError(e);
            }

            DEFAULT_HOST = properties.getProperty(HOST_PROPERTY);
            DEFAULT_PORT = properties.getProperty(PORT_PROPERTY);
            DEFAULT_DB = properties.getProperty(DB_PROPERTY);
            DEFAULT_USER = properties.getProperty(USER_PROPERTY);
            DEFAULT_PASSWORD = properties.getProperty(PASSWORD_PROPERTY);

            Objects.requireNonNull(DEFAULT_HOST);
            Objects.requireNonNull(DEFAULT_PORT);
            Objects.requireNonNull(DEFAULT_DB);
            Objects.requireNonNull(DEFAULT_USER);
            Objects.requireNonNull(DEFAULT_PASSWORD);
        }

        private String host = DEFAULT_HOST;
        private String port = DEFAULT_PORT;
        private String db = DEFAULT_DB;
        private String user = DEFAULT_USER;
        private String password = DEFAULT_PASSWORD;

        private Builder()
        {
        }

        public PostgresqlTraceDbFactory build()
        {
            return new PostgresqlTraceDbFactory(this);
        }
    }
}
