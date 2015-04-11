package com.github.fge.grappa.debugger.model.matches;

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
            final int c1 = nrMatches(o1);
            final int c2 = nrMatches(o2);
            return Integer.compare(c2, c1);
        };

    private int nonEmptyMatches = 0;
    private int emptyMatches = 0;
    private int failedMatches = 0;

    private int totalMatches = 0;

    private Double topOne = null;
    private Double topFive = null;
    private Double topTen = null;

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
        return Collections.unmodifiableList(allStats);
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

        int accumulated =  nrMatches(allStats.get(0));
        topOne = 100.0 * accumulated / totalMatches;

        if (nrMatchers < 5)
            return this;

        for (int i = 1; i < 5; i++)
            accumulated += nrMatches(allStats.get(i));
        topFive = 100.0 * accumulated / totalMatches;

        if (nrMatchers < 10)
            return this;

        for (int i = 5; i < 10; i++)
            accumulated += nrMatches(allStats.get(i));
        topTen = 100.0 * accumulated / totalMatches;

        return this;
    }

    private static int nrMatches(final MatchStatistics stats)
    {
        return stats.getEmptyMatches() + stats.getFailedMatches()
            + stats.getNonEmptyMatches();
    }
}
