package com.github.fge.grappa.debugger.postgresql.db.create;

import com.github.fge.grappa.debugger.postgresql.db.PostgresqlTraceDbFactory;
import org.flywaydb.core.Flyway;
import org.jooq.util.GenerationTool;
import org.jooq.util.jaxb.Configuration;
import org.jooq.util.jaxb.Database;
import org.jooq.util.jaxb.Generator;
import org.jooq.util.jaxb.Jdbc;
import org.jooq.util.jaxb.Target;

// Run at the ROOT of the project
public final class PostgresqlDbCreator
{
    private final String jdbcUrl;
    private final String user;
    private final String password;

    public PostgresqlDbCreator()
    {
        final PostgresqlTraceDbFactory factory
            = PostgresqlTraceDbFactory.defaultFactory();

        jdbcUrl = factory.getJdbcUrl();
        user = factory.getUser();
        password = factory.getPassword();
    }

    public int initdb()
    {
        final Flyway flyway = new Flyway();

        flyway.setLocations("classpath:db/postgresql");
        flyway.setDataSource(jdbcUrl, user, password);

        return flyway.migrate();
    }

    public void generateSources()
        throws Exception
    {
        final Jdbc jdbc = new Jdbc().withDriver("org.postgresql.Driver")
            .withUrl(jdbcUrl).withUser(user).withPassword(password);

        final Database database = new Database()
            .withName("org.jooq.util.postgres.PostgresDatabase")
            .withIncludes(".*").withExcludes("schema_version")
            .withInputSchema("public");

        final String pkg = "com.github.fge.grappa.debugger.postgresql.jooq";
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
        final PostgresqlDbCreator creator = new PostgresqlDbCreator();

        final int nrMigrations = creator.initdb();
        if (nrMigrations != 0)
            creator.generateSources();

        System.exit(0);
    }
}
