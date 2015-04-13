package com.github.fge.grappa.debugger.postgresql.model;

import com.github.fge.grappa.debugger.model.tree.ParseTreeNode;
import com.github.fge.grappa.debugger.model.tree.RuleInfo;
import com.github.fge.grappa.matchers.MatcherType;
import org.jooq.Record;
import org.jooq.RecordMapper;

import static com.github.fge.grappa.debugger.postgresql.jooq.Tables.MATCHERS;
import static com.github.fge.grappa.debugger.postgresql.jooq.Tables.NODES;

public final class ParseTreeNodeMapper
    implements RecordMapper<Record, ParseTreeNode>
{
    public static final RecordMapper<Record, ParseTreeNode> INSTANCE
        = new ParseTreeNodeMapper();

    private ParseTreeNodeMapper()
    {
    }

    @SuppressWarnings("AutoUnboxing")
    @Override
    public ParseTreeNode map(final Record record)
    {
        final MatcherType matcherType
            = MatcherType.valueOf(record.getValue(MATCHERS.MATCHER_TYPE));
        final RuleInfo ruleInfo = new RuleInfo(
            record.getValue(MATCHERS.CLASS_NAME),
            matcherType,
            record.getValue(MATCHERS.NAME)
        );

        final boolean success = record.getValue(NODES.SUCCESS) == 1;
        final boolean hasChildren
            = record.getValue("nrChildren", Integer.class) >= 1;

        return new ParseTreeNode(
            record.getValue(NODES.PARENT_ID),
            record.getValue(NODES.ID),
            record.getValue(NODES.LEVEL),
            success,
            ruleInfo,
            record.getValue(NODES.START_INDEX),
            record.getValue(NODES.END_INDEX),
            record.getValue(NODES.TIME),
            hasChildren
        );
    }
}
