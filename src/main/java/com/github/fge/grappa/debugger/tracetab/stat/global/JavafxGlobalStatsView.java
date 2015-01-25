package com.github.fge.grappa.debugger.tracetab.stat.global;

import com.github.fge.grappa.debugger.statistics.RuleMatchingStats;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collection;
import java.util.Objects;

@ParametersAreNonnullByDefault
public final class JavafxGlobalStatsView
    implements GlobalStatsView
{
    private final GlobalStatsDisplay display;

    public JavafxGlobalStatsView(final GlobalStatsDisplay display)
    {
        this.display = Objects.requireNonNull(display);
    }

    @Override
    public void loadStats(final Collection<RuleMatchingStats> stats)
    {
        display.stats.getItems().setAll(stats);
    }
}
