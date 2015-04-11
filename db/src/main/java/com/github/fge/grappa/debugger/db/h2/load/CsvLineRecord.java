package com.github.fge.grappa.debugger.db.h2.load;

import org.jooq.Record;

import java.util.function.Function;
import java.util.regex.Pattern;

public abstract class CsvLineRecord<R extends Record>
    implements Function<String, R>
{
    private final Pattern SEMICOLON = Pattern.compile(";");

    protected final int limit;

    protected CsvLineRecord()
    {
        this(-1);
    }

    protected CsvLineRecord(final int limit)
    {
        this.limit = limit;
    }

    @Override
    public final R apply(final String s)
    {
        final String[] parts = SEMICOLON.split(s, limit);
        return doApply(parts);
    }

    @SuppressWarnings("MethodCanBeVariableArityMethod")
    protected abstract R doApply(final String[] parts);
}
