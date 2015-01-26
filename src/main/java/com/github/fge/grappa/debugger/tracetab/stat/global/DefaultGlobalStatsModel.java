package com.github.fge.grappa.debugger.tracetab.stat.global;

import com.github.fge.grappa.debugger.stats.global.GlobalParseInfo;
import com.github.fge.grappa.debugger.stats.global.RuleMatchingStats;
import com.github.fge.grappa.debugger.stats.global.RuleMatchingStatsProcessor;
import com.github.fge.grappa.debugger.tracetab.TraceTabModel;
import com.github.fge.grappa.trace.ParseRunInfo;
import com.github.fge.grappa.trace.TraceEvent;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class DefaultGlobalStatsModel
    implements GlobalStatsModel
{
    private final Collection<RuleMatchingStats> stats;
    private final ParseRunInfo info;
    private final GlobalParseInfo parseInfo;


    public DefaultGlobalStatsModel(final TraceTabModel model)
    {
        Objects.requireNonNull(model);

        info = model.getInfo();

        final RuleMatchingStatsProcessor processor
            = new RuleMatchingStatsProcessor();
        final List<TraceEvent> events = model.getEvents();

        final long totalParseTime
            = events.get(events.size() - 1).getNanoseconds();
        final int treeDepth = events.stream().peek(processor::process)
            .mapToInt(TraceEvent::getLevel).max().getAsInt();

        stats = processor.getStats();

        int nonEmptyMatches = 0;
        int emptyMatches = 0;
        int failedMatches = 0;

        for (final RuleMatchingStats stat: stats) {
            nonEmptyMatches += stat.getNonEmptyMatches();
            emptyMatches += stat.getEmptyMatches();
            failedMatches += stat.getFailures();
        }

        parseInfo = new GlobalParseInfo(nonEmptyMatches, emptyMatches,
            failedMatches, treeDepth, totalParseTime);
    }

    @Nonnull
    @Override
    public GlobalParseInfo getGlobalParseInfo()
    {
        return parseInfo;
    }

    @Nonnull
    @Override
    public ParseRunInfo getParseRunInfo()
    {
        return info;
    }

    @Nonnull
    @Override
    public Collection<RuleMatchingStats> getRuleStats()
    {
        return Collections.unmodifiableCollection(stats);
    }
}
