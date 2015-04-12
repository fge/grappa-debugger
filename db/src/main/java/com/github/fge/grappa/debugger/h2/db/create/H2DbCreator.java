package com.github.fge.grappa.debugger.h2.db.create;

import com.github.fge.filesystem.MoreFiles;
import com.github.fge.filesystem.RecursionMode;
import org.flywaydb.core.Flyway;
import org.jooq.util.GenerationTool;
import org.jooq.util.jaxb.Configuration;
import org.jooq.util.jaxb.Database;
import org.jooq.util.jaxb.Generator;
import org.jooq.util.jaxb.Jdbc;
import org.jooq.util.jaxb.Target;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

// Run at the ROOT of the project
public final class H2DbCreator
{
    private static final String H2_USERNAME = "sa";
    private static final String H2_PASSWORD = "";

    private static final String H2_JDBC_URL_FORMAT
        = "jdbc:h2:%s;LOG=0;LOCK_MODE=0;UNDO_LOG=0;CACHE_SIZE=131072";

    private final Path dbpath;
    private final String jdbcUrl;

    public H2DbCreator()
        throws IOException
    {
        dbpath = Files.createTempDirectory("grappa-debugger").toRealPath();

        jdbcUrl = String.format(H2_JDBC_URL_FORMAT, dbpath);
    }

    public int initdb()
    {
        final Flyway flyway = new Flyway();

        flyway.setLocations("classpath:db/h2");
        flyway.setDataSource(jdbcUrl, H2_USERNAME, H2_PASSWORD);

        return flyway.migrate();
    }

    public void generateSources()
        throws Exception
    {
        final Jdbc jdbc = new Jdbc().withDriver("org.h2.Driver")
            .withUrl(jdbcUrl).withUser(H2_USERNAME)
            .withPassword(H2_PASSWORD);

        final Database database = new Database()
            .withName("org.jooq.util.h2.H2Database")
            .withIncludes(".*").withExcludes("schema_version")
            .withInputSchema("PUBLIC");

        final String pkg = "com.github.fge.grappa.debugger.h2.jooq";
        final String dir = "db/src/main/java";
        final Target target = new Target().withPackageName(pkg)
            .withDirectory(dir);

        final Generator generator = new Generator()
            .withName("org.jooq.util.DefaultGenerator")
            .withDatabase(database)
            .withTarget(target);

        final Configuration cfg = new Configuration().withJdbc(jdbc)
            .withGenerator(generator);

        GenerationTool.generate(cfg);
    }

    public static void main(final String... args)
        throws Exception
    {
        final H2DbCreator creator = new H2DbCreator();

        try {
            creator.initdb();
            creator.generateSources();
        } finally {
            MoreFiles.deleteRecursive(creator.dbpath, RecursionMode.KEEP_GOING);
        }

        System.exit(0);
    }
}
