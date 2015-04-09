package com.github.fge.grappa.debugger;

import org.flywaydb.core.Flyway;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class FlywayTest
{
    private static final String H2_URI_PREFIX = "jdbc:h2:";
    private static final String H2_URI_POSTFIX
        = ";LOG=0;LOCK_MODE=0;UNDO_LOG=0;CACHE_SIZE=131072";
    private static final String H2_USERNAME = "sa";
    private static final String H2_PASSWORD = "";

    public static void main(final String... args)
        throws IOException
    {
        final Path dbPath = Files.createTempDirectory("grappa-debugger");
        final String url = H2_URI_PREFIX + dbPath.resolve("db").toAbsolutePath()
            + H2_URI_POSTFIX;

        final Flyway flyway = new Flyway();

        flyway.setDataSource(url, H2_USERNAME, H2_PASSWORD);

        flyway.migrate();
    }
}
