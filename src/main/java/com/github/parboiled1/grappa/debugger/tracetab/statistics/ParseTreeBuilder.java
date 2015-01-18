package com.github.parboiled1.grappa.debugger.tracetab.statistics;

import com.github.parboiled1.grappa.trace.TraceEvent;
import com.github.parboiled1.grappa.trace.TraceEventType;

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

    // TODO: this assumes that the events list is never empty
    // (but OTOH this is assumed never to be called if this is the case)
    @SuppressWarnings({ "AutoBoxing", "TypeMayBeWeakened" })
    private static ParseNode process(@Untainted final List<TraceEvent> events)
    {
        final Map<Integer, ParseNode> nodes = new HashMap<>();

        nodes.put(-1, new ParseNode("WTF", Integer.MIN_VALUE));

        int level;
        TraceEventType type;
        ParseNode node;

        for (final TraceEvent event: events) {
            level = event.getLevel();
            type = event.getType();

            if (type == TraceEventType.BEFORE_MATCH) {
                node = new ParseNode(event.getMatcher(), event.getIndex());
                nodes.put(level, node);
                continue;
            }

            node = nodes.get(level);
            node.setEnd(event.getIndex());
            node.setSuccess(type == TraceEventType.MATCH_SUCCESS);
            nodes.get(level - 1).addChild(node);
        }

        return nodes.get(0);
    }
}
