package com.github.fge.grappa.debugger.h2.model;

import com.github.fge.grappa.buffers.InputBuffer;
import com.github.fge.grappa.debugger.h2.jooq.tables.Nodes;
import com.github.fge.grappa.debugger.model.TraceModel;
import com.github.fge.grappa.debugger.model.TraceModelException;
import com.github.fge.grappa.debugger.model.matches.MatchesData;
import com.github.fge.grappa.debugger.model.rules.PerClassStatistics;
import com.github.fge.grappa.debugger.model.tree.ParseTreeNode;
import com.github.fge.grappa.matchers.MatcherType;
import com.github.fge.grappa.support.IndexRange;
import org.jooq.CaseConditionStep;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.impl.DSL;

import javax.annotation.Nonnull;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.github.fge.grappa.debugger.h2.jooq.Tables.MATCHERS;
import static com.github.fge.grappa.debugger.h2.jooq.Tables.NODES;

@SuppressWarnings({ "AutoUnboxing", "AutoBoxing" })
public final class H2TraceModel
    implements TraceModel
{
    private static final Condition EMPTY_MATCHES_CONDITION = NODES.SUCCESS.eq(1)
        .and(NODES.START_INDEX.equal(NODES.END_INDEX));
    private static final Condition NONEMPTY_MATCHES_CONDITION
        = NODES.SUCCESS.eq(1).and(NODES.START_INDEX.ne(NODES.END_INDEX));
    private static final Condition FAILED_MATCHES_CONDITION
        = NODES.SUCCESS.eq(0);

    private final DSLContext jooq;
    private final InputBuffer inputBuffer;

    public H2TraceModel(final DSLContext jooq, final InputBuffer inputBuffer)
    {
        this.jooq = jooq;
        this.inputBuffer = inputBuffer;
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
        final Nodes nodes2 = NODES.as("nodes2");

        final Field<Integer> nrChildren = jooq.select(DSL.count(nodes2.ID))
            .from(nodes2)
            .where(nodes2.PARENT_ID.eq(NODES.ID))
            .asField("nrChildren");

        return jooq.select(NODES.PARENT_ID, NODES.ID, NODES.LEVEL,
            NODES.SUCCESS, MATCHERS.CLASS_NAME, MATCHERS.MATCHER_TYPE,
            MATCHERS.NAME, NODES.START_INDEX, NODES.END_INDEX, NODES.TIME,
            nrChildren)
            .from(NODES, MATCHERS)
            .where(MATCHERS.ID.eq(NODES.MATCHER_ID))
            .and(condition)
            .fetch().map(ParseTreeNodeMapper.INSTANCE);
    }

    @Nonnull
    @Override
    public Map<MatcherType, Integer> getMatchersByType()
    {
        final Map<MatcherType, Integer> ret = new EnumMap<>(MatcherType.class);

        jooq.select(MATCHERS.MATCHER_TYPE, DSL.count())
            .from(MATCHERS)
            .groupBy(MATCHERS.MATCHER_TYPE)
            .forEach(r -> ret.put(MatcherType.valueOf(r.value1()), r.value2()));

        return ret;
    }

    @Nonnull
    @Override
    public List<PerClassStatistics> getRulesByClass()
    {
        return jooq.select(MATCHERS.CLASS_NAME, DSL.count().as("nrCalls"),
            DSL.countDistinct(NODES.MATCHER_ID).as("nrRules"))
            .from(MATCHERS, NODES)
            .where(NODES.MATCHER_ID.eq(MATCHERS.ID))
            .groupBy(MATCHERS.CLASS_NAME)
            .fetch()
            .map(PerClassStatisticsMapper.INSTANCE);
    }

    @Nonnull
    @Override
    public Map<Integer, Integer> getDepthMap(final int startLine,
        final int wantedLines)
    {
        final List<IndexRange> ranges
            = IntStream.range(startLine, startLine + wantedLines)
            .mapToObj(inputBuffer::getLineRange)
            .collect(Collectors.toList());

        final int startIndex = ranges.get(0).start;
        final int endIndex = ranges.get(ranges.size() - 1).end;
        final Condition indexCondition = NODES.START_INDEX.lt(endIndex)
            .and(NODES.END_INDEX.ge(startIndex));

        final Field<Integer> lineField = getLineField(startLine, ranges);

        final Map<Integer, Integer> ret = new HashMap<>();

        jooq.select(lineField, DSL.max(NODES.LEVEL))
            .from(NODES)
            .where(indexCondition)
            .groupBy(lineField)
            .forEach(r -> ret.put(r.value1(), r.value2() + 1));

        IntStream.range(startLine, startLine + wantedLines)
            .forEach(line -> ret.putIfAbsent(line, 0));

        return ret;
    }

    @Nonnull
    @Override
    public MatchesData getMatchesData()
    {
        final Field<Integer> emptyMatches = DSL.decode()
            .when(EMPTY_MATCHES_CONDITION, 1).otherwise(0);
        final Field<Integer> nonEmptyMatches = DSL.decode()
            .when(NONEMPTY_MATCHES_CONDITION, 1).otherwise(0);
        final Field<Integer> failedMatches = DSL.decode()
            .when(FAILED_MATCHES_CONDITION, 1).otherwise(0);

        return jooq.select(MATCHERS.NAME, MATCHERS.MATCHER_TYPE,
            MATCHERS.CLASS_NAME,
            DSL.sum(emptyMatches).as("emptyMatches"),
            DSL.sum(nonEmptyMatches).as("nonEmptyMatches"),
            DSL.sum(failedMatches).as("failedMatches"))
            .from(MATCHERS, NODES)
            .where(MATCHERS.ID.eq(NODES.MATCHER_ID))
            .groupBy(MATCHERS.NAME, MATCHERS.MATCHER_TYPE, MATCHERS.CLASS_NAME)
            .fetch().stream()
            .map(MatchStatisticsMapper.INSTANCE::map)
            .collect(MatchesData.asCollector());
    }

    private Field<Integer> getLineField(final int startLine,
        final List<IndexRange> ranges)
    {
        CaseConditionStep<Integer> step = DSL.decode()
            .when(activeThisRange(ranges.get(0)), startLine);

        final int size = ranges.size();

        for (int i = 1; i < size; i++)
            step = step.when(activeThisRange(ranges.get(i)), startLine + i);

        return step.as("line");
    }

    private static Condition activeThisRange(final IndexRange range)
    {
        return NODES.START_INDEX.lt(range.end)
            .and(NODES.END_INDEX.ge(range.start));
    }
}
