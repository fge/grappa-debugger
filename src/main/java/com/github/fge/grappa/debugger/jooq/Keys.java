package com.github.fge.grappa.debugger.jooq;

import com.github.fge.grappa.debugger.jooq.tables.Matchers;
import com.github.fge.grappa.debugger.jooq.tables.Nodes;
import com.github.fge.grappa.debugger.jooq.tables.records.MatchersRecord;
import com.github.fge.grappa.debugger.jooq.tables.records.NodesRecord;
import org.jooq.ForeignKey;
import org.jooq.UniqueKey;
import org.jooq.impl.AbstractKeys;

public final class Keys
{
    public static final UniqueKey<MatchersRecord> CONSTRAINT_4
        = UniqueKeys0.CONSTRAINT_4;
    public static final UniqueKey<NodesRecord> CONSTRAINT_47
        = UniqueKeys0.CONSTRAINT_47;

    public static final ForeignKey<NodesRecord, MatchersRecord>
        CONSTRAINT_470 = ForeignKeys0.CONSTRAINT_470;

    private Keys()
    {
    }

    private static class UniqueKeys0
        extends AbstractKeys
    {
        public static final UniqueKey<MatchersRecord> CONSTRAINT_4
            = createUniqueKey(Matchers.MATCHERS, Matchers.MATCHERS.ID);
        public static final UniqueKey<NodesRecord> CONSTRAINT_47
            = createUniqueKey(Nodes.NODES, Nodes.NODES.ID);
    }

    private static class ForeignKeys0
        extends AbstractKeys
    {
        public static final ForeignKey<NodesRecord, MatchersRecord>
            CONSTRAINT_470 = AbstractKeys.createForeignKey(CONSTRAINT_4,
            Nodes.NODES, Nodes.NODES.MATCHER_ID);
    }
}
