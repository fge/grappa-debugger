package com.github.fge.grappa.debugger.statistics;

import com.github.fge.grappa.trace.TraceEvent;
import com.github.fge.grappa.trace.TraceEventType;

import java.util.HashMap;
import java.util.Map;

public final class ParseTreeProcessor
    implements TraceEventProcessor
{
    private final Map<Integer, ParseNode> nodes = new HashMap<>();
    private final Map<Integer, Long> times = new HashMap<>();

    public ParseTreeProcessor()
    {
        nodes.put(-1, new ParseNode("WTF", Integer.MIN_VALUE, 0));
    }

    @SuppressWarnings({ "AutoBoxing", "AutoUnboxing" })
    @Override
    public void process(final TraceEvent event)
    {
        final TraceEventType type = event.getType();
        final int level = event.getLevel();
        final ParseNode node;

        if (type == TraceEventType.BEFORE_MATCH) {
            node = new ParseNode(event.getMatcher(), event.getIndex(),
                level);
            nodes.put(level, node);
            times.put(level, event.getNanoseconds());
            return;
        }

        node = nodes.get(level);
        node.setEnd(event.getIndex());
        final boolean success = type == TraceEventType.MATCH_SUCCESS;
        node.setSuccess(success);
        node.setNanos(event.getNanoseconds() - times.get(level));
        nodes.get(level - 1).addChild(node);
    }

    public ParseNode getRootNode()
    {
        return nodes.get(0);
    }
}
