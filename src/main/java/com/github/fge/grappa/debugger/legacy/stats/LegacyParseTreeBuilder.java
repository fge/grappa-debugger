package com.github.fge.grappa.debugger.legacy.stats;


import com.github.fge.grappa.trace.TraceEventType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class LegacyParseTreeBuilder
{
    private final LegacyParseNode rootNode;
    private int nrEmptyMatches = 0;

    public LegacyParseTreeBuilder(final List<LegacyTraceEvent> events)
    {
        rootNode = process(events);
    }

    public LegacyParseNode getRootNode()
    {
        return rootNode;
    }

    public int getNrEmptyMatches()
    {
        return nrEmptyMatches;
    }

    @SuppressWarnings({ "AutoBoxing", "TypeMayBeWeakened" })
    private LegacyParseNode process(final List<LegacyTraceEvent> events)
    {
        final Map<Integer, LegacyParseNode> nodes = new HashMap<>();
        final Map<Integer, Long> times = new HashMap<>();

        nodes.put(-1, new LegacyParseNode("WTF", Integer.MIN_VALUE, 0));

        int level;
        TraceEventType type;
        LegacyParseNode node;

        for (final LegacyTraceEvent event: events) {
            level = event.getLevel();
            type = event.getType();

            if (type == TraceEventType.BEFORE_MATCH) {
                node = new LegacyParseNode(event.getMatcher(), event.getIndex(),
                    level);
                nodes.put(level, node);
                times.put(level, event.getNanoseconds());
                continue;
            }

            node = nodes.get(level);
            node.setEnd(event.getIndex());
            final boolean success = type == TraceEventType.MATCH_SUCCESS;
            node.setSuccess(success);
            if (success && node.getStart() == node.getEnd())
                nrEmptyMatches++;
            //noinspection AutoUnboxing
            node.setNanos(event.getNanoseconds() - times.get(level));
            nodes.get(level - 1).addChild(node);
        }

        return nodes.get(0);
    }
}
