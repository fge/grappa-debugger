package com.github.fge.grappa.debugger.h2.model;

import com.github.fge.grappa.debugger.model.TraceModel;
import com.github.fge.grappa.debugger.model.TraceModelException;
import com.github.fge.grappa.debugger.model.matches.MatchesData;
import com.github.fge.grappa.debugger.model.rules.PerClassStatistics;
import com.github.fge.grappa.debugger.model.tree.ParseTreeNode;
import com.github.fge.grappa.debugger.model.tree.ParseTreeNodeMapper;
import com.github.fge.grappa.matchers.MatcherType;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record2;
import org.jooq.Table;
import org.jooq.impl.DSL;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;

import static com.github.fge.grappa.debugger.h2.jooq.Tables.MATCHERS;
import static com.github.fge.grappa.debugger.h2.jooq.Tables.NODES;

public final class H2TraceModel
    implements TraceModel
{
    private final DSLContext jooq;

    public H2TraceModel(final DSLContext jooq)
    {
        this.jooq = jooq;
    }

    @Nonnull
    @Override
    public List<ParseTreeNode> getNodeChildren(final int nodeId)
    {
        final Condition condition = NODES.PARENT_ID.eq(nodeId);
        return getNodes(condition);
    }

    @Nonnull
    @Override
    public ParseTreeNode getNodeById(final int id)
    {
        final Condition condition = NODES.ID.eq(id);
        final List<ParseTreeNode> nodes = getNodes(condition);

        if (nodes.size() != 1)
            throw new TraceModelException("expected only 1 record");

        return nodes.get(0);
    }

    private List<ParseTreeNode> getNodes(final Condition condition)
    {
        final Field<Integer> children = DSL.count().as("nrChildren");

        final Table<Record2<Integer, Integer>> subnodes
            = jooq.select(NODES.PARENT_ID, children)
            .from(NODES)
            .groupBy(NODES.PARENT_ID)
            .asTable();

        final Field<Integer> parentId = subnodes.field(NODES.PARENT_ID);
        final Field<Integer> nrChildren = subnodes.field(children);

        return jooq.select(NODES.PARENT_ID, NODES.ID, NODES.LEVEL,
            NODES.SUCCESS, MATCHERS.CLASS_NAME, MATCHERS.MATCHER_TYPE,
            MATCHERS.NAME, NODES.START_INDEX, NODES.END_INDEX, NODES.TIME,
            nrChildren)
            .from(NODES, MATCHERS, subnodes)
            .where(MATCHERS.ID.eq(NODES.MATCHER_ID))
            .and(parentId.eq(NODES.ID))
            .and(condition)
            .fetch().map(ParseTreeNodeMapper.INSTANCE);
    }

    @Nonnull
    @Override
    public Map<MatcherType, Integer> getMatchersByType()
    {
        // TODO
        return null;
    }

    @Nonnull
    @Override
    public List<PerClassStatistics> getRulesByClass()
    {
        // TODO
        return null;
    }

    @Nonnull
    @Override
    public Map<Integer, Integer> getDepthMap(final int startLine,
        final int wantedLines)
    {
        // TODO
        return null;
    }

    @Nonnull
    @Override
    public MatchesData getMatchesData()
    {
        // TODO
        return null;
    }
}
