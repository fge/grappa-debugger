package com.github.fge.grappa.debugger.tracetab.stat.global;

import com.github.fge.grappa.debugger.stats.RuleMatchingStats;
import com.github.fge.grappa.debugger.stats.RuleMatchingStatsProcessor;
import com.github.fge.grappa.debugger.tracetab.TraceTabModel;
import com.github.fge.grappa.internal.NonFinalForTesting;
import com.github.fge.grappa.trace.ParseRunInfo;
import com.github.fge.grappa.trace.TraceEvent;
import com.google.common.annotations.VisibleForTesting;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@ParametersAreNonnullByDefault
@NonFinalForTesting
public class GlobalStatsPresenter
{
    private final Collection<RuleMatchingStats> stats;
    private final ParseRunInfo info;

    private int nonEmptyMatches = 0;
    private int emptyMatches = 0;
    private int failedMatches = 0;
    private int totalMatches = 0;

    private int treeDepth = 0;
    private long totalParseTime = 0;


    private GlobalStatsView view;

    public GlobalStatsPresenter(final TraceTabModel model)
    {
        Objects.requireNonNull(model);
        info = model.getInfo();

        final RuleMatchingStatsProcessor processor
            = new RuleMatchingStatsProcessor();
        final List<TraceEvent> events = model.getEvents();

        totalParseTime = events.get(events.size() - 1).getNanoseconds();
        treeDepth = events.stream().peek(processor::process)
            .mapToInt(TraceEvent::getLevel).max().getAsInt();

        stats = processor.getStats();

        for (final RuleMatchingStats stat: stats) {
            nonEmptyMatches += stat.getNonEmptyMatches();
            emptyMatches += stat.getEmptyMatches();
            failedMatches += stat.getFailures();
        }

        totalMatches = nonEmptyMatches + emptyMatches + failedMatches;
    }

    @VisibleForTesting
    GlobalStatsPresenter(final Collection<RuleMatchingStats> stats,
        final ParseRunInfo info)
    {
        this.stats = Objects.requireNonNull(stats);
        this.info = Objects.requireNonNull(info);
    }

    public void setView(final GlobalStatsView view)
    {
        this.view = Objects.requireNonNull(view);
    }

    public void loadStats()
    {
        view.loadStats(stats);
        view.loadInfo(info, totalMatches, treeDepth, totalParseTime);
        view.loadPieChart(failedMatches, emptyMatches, nonEmptyMatches);
    }
}
