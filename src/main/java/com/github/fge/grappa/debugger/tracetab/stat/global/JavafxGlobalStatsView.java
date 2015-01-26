package com.github.fge.grappa.debugger.tracetab.stat.global;

import com.github.fge.grappa.debugger.javafx.Utils;
import com.github.fge.grappa.debugger.statistics.RuleMatchingStats;
import com.github.fge.grappa.trace.ParseRunInfo;
import javafx.scene.chart.PieChart.Data;

import javax.annotation.ParametersAreNonnullByDefault;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
        display.nrRules.setText(String.valueOf(stats.size()));
    }

    @SuppressWarnings("AutoBoxing")
    @Override
    public void loadInfo(final ParseRunInfo info, final int totalMatches,
        final int treeDepth, final long totalParseTime)
    {
        final Instant instant = Instant.ofEpochMilli(info.getStartDate());
        // TODO: record tz info in the JSON
        final LocalDateTime time
            = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());

        display.parseDate.setText(time.toString());

        display.totalParseTime.setText(Utils.nanosToString(totalParseTime));

        display.treeDepth.setText(String.valueOf(treeDepth));

        display.invPerLine.setText(String.format("%.02f",
            (double) totalMatches / info.getNrLines()));
        display.invPerChar.setText(String.format("%.02f",
            (double) totalMatches / info.getNrChars()));
    }

    @SuppressWarnings("AutoBoxing")
    @Override
    public void loadPieChart(final int failedMatches, final int emptyMatches,
        final int nonEmptyMatches)
    {
        final int totalInvocations = failedMatches + emptyMatches
            + nonEmptyMatches;
        final List<Data> list = new ArrayList<>(3);

        int nr;
        double percent;
        String fmt;

        /*
         * Failures
         */
        nr = failedMatches;
        percent = 100.0 * nr / totalInvocations;
        fmt = String.format("Failures (%d - %.02f%%)", nr, percent);
        list.add(new Data(fmt, percent));

        /*
         * Empty
         */
        nr = emptyMatches;
        percent = 100.0 * nr / totalInvocations;
        fmt = String.format("Empty matches (%d - %.02f%%)", nr, percent);
        list.add(new Data(fmt, percent));

        /*
         * Non empty
         */
        nr = nonEmptyMatches;
        percent = 100.0 * nr / totalInvocations;
        fmt = String.format("Non empty matches (%d; %.02f%%)", nr, percent);
        list.add(new Data(fmt, percent));

        display.matchChart.getData().setAll(list);

        fmt = String.format("Rule rundown (%d total)", totalInvocations);
        display.matchChart.setTitle(fmt);
    }
}
