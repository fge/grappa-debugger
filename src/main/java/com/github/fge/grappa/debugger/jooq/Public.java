package com.github.fge.grappa.debugger.jooq;

import com.github.fge.grappa.debugger.jooq.tables.Matchers;
import com.github.fge.grappa.debugger.jooq.tables.Nodes;
import org.jooq.Table;
import org.jooq.impl.SchemaImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("TypeMayBeWeakened")
public final class Public
    extends SchemaImpl
{

    private static final long serialVersionUID = -209875434;

    public static final Public PUBLIC = new Public();

    private Public()
    {
        super("PUBLIC");
    }

    @Override
    public List<Table<?>> getTables()
    {
        final List<Table<?>> result = new ArrayList<>();
        result.addAll(getTables0());
        return result;
    }

    private List<Table<?>> getTables0()
    {
        return Arrays.<Table<?>>asList(Matchers.MATCHERS, Nodes.NODES);
    }
}
