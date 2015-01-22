package com.github.fge.grappa.debugger.tracetab.statistics;


import com.github.fge.grappa.debugger.legacy.TraceEvent;
import com.github.fge.grappa.trace.TraceEventType;

import javax.annotation.Untainted;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ParseTreeBuilder
{
    private final ParseNode rootNode;

    public ParseTreeBuilder(final List<TraceEvent> events)
    {
        rootNode = process(events);
    }

    public ParseNode getRootNode()
    {
        return rootNode;
    }

    @SuppressWarnings({ "AutoBoxing", "TypeMayBeWeakened" })
    private static ParseNode process(@Untainted final List<TraceEvent> events)
    {
        final Map<Integer, ParseNode> nodes = new HashMap<>();
        final Map<Integer, Long> times = new HashMap<>();

        nodes.put(-1, new ParseNode("WTF", Integer.MIN_VALUE, 0));

        int level;
        TraceEventType type;
        ParseNode node;

        for (final TraceEvent event: events) {
            level = event.getLevel();
            type = event.getType();

            if (type == TraceEventType.BEFORE_MATCH) {
                node = new ParseNode(event.getMatcher(), event.getIndex(),
                    level);
                nodes.put(level, node);
                times.put(level, event.getNanoseconds());
                continue;
            }

            node = nodes.get(level);
            node.setEnd(event.getIndex());
            node.setSuccess(type == TraceEventType.MATCH_SUCCESS);
            //noinspection AutoUnboxing
            node.setNanos(event.getNanoseconds() - times.get(level));
            nodes.get(level - 1).addChild(node);
        }

        return nodes.get(0);
    }
}
