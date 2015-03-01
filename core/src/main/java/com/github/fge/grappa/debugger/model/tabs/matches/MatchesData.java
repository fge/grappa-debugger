package com.github.fge.grappa.debugger.model.tabs.matches;

import com.github.fge.grappa.debugger.model.db.MatchStatistics;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collector;

public final class MatchesData
{
    private static final Comparator<MatchStatistics> STATISTICS_COMPARATOR
        = (o1, o2) -> {
            final int c1 = o1.getEmptyMatches() + o1.getFailedMatches()
                + o1.getNonEmptyMatches();
            final int c2 = o2.getEmptyMatches() + o2.getFailedMatches()
                + o2.getNonEmptyMatches();
            return Integer.compare(c2, c1);
        };

    private int nonEmptyMatches = 0;
    private int emptyMatches = 0;
    private int failedMatches = 0;

    private int totalMatches = 0;

    private Double topOne = null;
    private Double topFive = null;
    private Double topTen = null;

    final Collector<MatchStatistics, MatchesData, MatchesData> collector
        = Collector.of(MatchesData::new, MatchesData::accumulate,
        MatchesData::combine, MatchesData::finish);

    public static Collector<MatchStatistics, MatchesData, MatchesData>
        asCollector()
    {
        return Collector.of(MatchesData::new, MatchesData::accumulate,
        MatchesData::combine, MatchesData::finish);
    }

    private final List<MatchStatistics> allStats = new ArrayList<>();

    public int getNonEmptyMatches()
    {
        return nonEmptyMatches;
    }

    public int getEmptyMatches()
    {
        return emptyMatches;
    }

    public int getFailedMatches()
    {
        return failedMatches;
    }

    public int getTotalMatches()
    {
        return totalMatches;
    }

    @Nullable
    public Double getTopOne()
    {
        return topOne;
    }

    @Nullable
    public Double getTopFive()
    {
        return topFive;
    }

    @Nullable
    public Double getTopTen()
    {
        return topTen;
    }

    public List<MatchStatistics> getAllStats()
    {
        return allStats;
    }

    // Accumulator
    public void accumulate(final MatchStatistics stats)
    {
        allStats.add(stats);
        nonEmptyMatches += stats.getNonEmptyMatches();
        emptyMatches += stats.getEmptyMatches();
        failedMatches += stats.getFailedMatches();
    }

    // Combiner
    public MatchesData combine(final MatchesData other)
    {
        allStats.addAll(other.allStats);
        nonEmptyMatches += other.nonEmptyMatches;
        emptyMatches += other.emptyMatches;
        failedMatches += other.failedMatches;
        return this;
    }

    // Finisher
    public MatchesData finish()
    {
        Collections.sort(allStats, STATISTICS_COMPARATOR);

        totalMatches = nonEmptyMatches + emptyMatches + failedMatches;
        final int nrMatchers = allStats.size();

        // Who knows...
        if (nrMatchers == 0)
            return this;

        int accumulated;
        MatchStatistics stats;

        stats = allStats.get(0);
        accumulated = stats.getEmptyMatches() + stats.getNonEmptyMatches()
            + stats.getFailedMatches();
        topOne = 100.0 * accumulated / totalMatches;

        if (nrMatchers < 5)
            return this;

        for (int i = 1; i < 5; i++) {
            stats = allStats.get(i);
            accumulated += stats.getEmptyMatches()
                + stats.getNonEmptyMatches() + stats.getFailedMatches();
        }
        topFive = 100.0 * accumulated / totalMatches;

        if (nrMatchers < 10)
            return this;

        for (int i = 5; i < 10; i++) {
            stats = allStats.get(i);
            accumulated += stats.getEmptyMatches()
                + stats.getNonEmptyMatches() + stats.getFailedMatches();
        }
        topTen = 100.0 * accumulated / totalMatches;

        return this;
    }
}
