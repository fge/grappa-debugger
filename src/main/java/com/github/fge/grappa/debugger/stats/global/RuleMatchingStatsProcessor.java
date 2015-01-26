package com.github.fge.grappa.debugger.stats.global;

import com.github.fge.grappa.debugger.stats.TraceEventProcessor;
import com.github.fge.grappa.trace.TraceEvent;
import com.github.fge.grappa.trace.TraceEventType;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public final class RuleMatchingStatsProcessor
    implements TraceEventProcessor
{
    private final Map<String, RuleMatchingStats> statsMap
        = new LinkedHashMap<>();
    private final Map<String, Integer> startingPositions = new HashMap<>();

    private RuleMatchingStats stats;

    @SuppressWarnings("AutoUnboxing")
    @Override
    public void process(final TraceEvent event)
    {
        final String ruleName = event.getMatcher();
        final TraceEventType type = event.getType();
        final int index = event.getIndex();

        stats = statsMap.get(ruleName);

        if (type == TraceEventType.BEFORE_MATCH) {
            if (stats == null) {
                stats = new RuleMatchingStats(ruleName, event.getMatcherClass(),
                    event.getMatcherType());
                statsMap.put(ruleName, stats);
            }
            startingPositions.put(ruleName, index);
            return;
        }


        if (type == TraceEventType.MATCH_FAILURE)
            stats.addFailure();
        else
            stats.addMatch(index == startingPositions.get(ruleName));
    }

    public Collection<RuleMatchingStats> getStats()
    {
        return Collections.unmodifiableCollection(statsMap.values());
    }
}
