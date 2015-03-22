package com.github.fge.grappa.debugger.database;

import com.github.fge.grappa.internal.NonFinalForTesting;
import org.jooq.DSLContext;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class TraceDatabase
{
    protected static final String NODES_CSV = "/nodes.csv";
    protected static final String MATCHERS_CSV = "/matchers.csv";

    protected final AtomicBoolean finished = new AtomicBoolean(false);

    protected final DSLContext jooq;

    protected TraceDatabase(final DSLContext jooq)
    {
        this.jooq = Objects.requireNonNull(jooq);
    }

    public final DSLContext getJooq()
    {
        return jooq;
    }

    @NonFinalForTesting
    public void loadAll()
        throws IOException
    {
        try {
            loadMatchers();
            loadNodes();
        } finally {
            finished.set(true);
        }
    }

    protected abstract void loadMatchers()
        throws IOException;

    protected abstract void loadNodes()
        throws IOException;

    @NonFinalForTesting
    public boolean isLoaded()
    {
        return finished.get();
    }
}
