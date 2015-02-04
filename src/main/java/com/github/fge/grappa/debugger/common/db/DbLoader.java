package com.github.fge.grappa.debugger.common.db;

import com.github.fge.filesystem.MoreFiles;
import com.github.fge.filesystem.RecursionMode;
import com.google.common.base.Charsets;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static com.github.fge.grappa.debugger.jooq.Tables.MATCHERS;
import static com.github.fge.grappa.debugger.jooq.Tables.NODES;

public final class DbLoader
    implements AutoCloseable
{
    private static final Charset UTF8 = Charsets.UTF_8;

    private static final String H2_URI_PREFIX = "jdbc:h2:";
    private static final String H2_URI_POSTFIX
        = ";LOG=0;LOCK_MODE=0;UNDO_LOG=0;CACHE_SIZE=131072";
    private static final String H2_USERNAME = "sa";
    private static final String H2_PASSWORD = "";

    private static final List<String> H2_DDL = Arrays.asList(
       "create table matchers ("
            + "id integer not null,"
            + "class_name varchar(255) not null,"
            + "matcher_type varchar(30) not null,"
            + "name varchar(1024) not null"
            + ");",
        "create table nodes ("
            + "id integer not null,"
            + "parent_id integer not null,"
            + "level integer not null,"
            + "success integer not null,"
            + "matcher_id integer not null,"
            + "start_index integer not null,"
            + "end_index integer not null,"
            + "time long not null"
            + ");",
        "alter table matchers add primary key(id);",
        "alter table nodes add primary key(id);",
        "alter table nodes add foreign key (matcher_id)"
            + "references matchers(id)"
    );


    private static final String MATCHERS_PATH = "/matchers.csv";
    private static final String NODES_PATH = "/nodes.csv";

    private final CsvMatchersRecord csvToMatcher = new CsvMatchersRecord();

    private final CsvNodesRecord csvToNode = new CsvNodesRecord();

    private final Path matchersPath;
    private final Path nodesPath;

    private final Path dbPath;

    private final Connection connection;
    private final DSLContext jooq;
    private final DbLoadStatus status;

    public DbLoader(final FileSystem zipfs, final DbLoadStatus status)
        throws IOException, SQLException
    {
        Objects.requireNonNull(zipfs);
        Objects.requireNonNull(status);

        this.status = status;

        matchersPath = zipfs.getPath(MATCHERS_PATH);
        nodesPath = zipfs.getPath(NODES_PATH);

        dbPath = Files.createTempDirectory("grappa-debugger");
        final String url = H2_URI_PREFIX + dbPath.resolve("db").toAbsolutePath()
            + H2_URI_POSTFIX;
        connection = DriverManager.getConnection(url, H2_USERNAME, H2_PASSWORD);
        jooq = DSL.using(connection, SQLDialect.H2);
        doDdl(jooq);
    }

    public DSLContext getJooq()
    {
        return jooq;
    }

    public DSLContext loadAll()
        throws IOException
    {
        try {
            insertMatchers(jooq);
            insertNodes(jooq);
            return jooq;
        } finally {
            status.setReady();
        }
    }

    private void doDdl(final DSLContext jooq)
    {
        H2_DDL.forEach(jooq::execute);
        jooq.createIndex("nodes_parent_id").on(NODES, NODES.PARENT_ID)
            .execute();
    }

    private void insertMatchers(final DSLContext jooq)
        throws IOException
    {
        try (
            final Stream<String> lines = Files.lines(matchersPath, UTF8);
        ) {
            lines.map(csvToMatcher)
                .peek(ignored -> status.incrementProcessedMatchers())
                .forEach(r -> jooq.insertInto(MATCHERS).set(r).execute());
        }
    }

    private void insertNodes(final DSLContext jooq)
        throws IOException
    {
        try (
            final Stream<String> lines = Files.lines(nodesPath, UTF8);
        ) {
            lines.map(csvToNode)
                .peek(ignored -> status.incrementProcessedNodes())
                .forEach(r -> jooq.insertInto(NODES).set(r).execute());
        }
    }

    @Override
    public void close()
        throws SQLException, IOException
    {
        IOException ioException = null;
        SQLException sqlException = null;

        try {
            connection.close();
        } catch (SQLException e) {
            sqlException = e;
        }

        try {
            MoreFiles.deleteRecursive(dbPath, RecursionMode.KEEP_GOING);
        } catch (IOException e) {
            ioException = e;
        }

        if (sqlException != null) {
            if (ioException != null)
                sqlException.addSuppressed(ioException);
            throw sqlException;
        }

        if (ioException != null)
            throw ioException;
    }
}
