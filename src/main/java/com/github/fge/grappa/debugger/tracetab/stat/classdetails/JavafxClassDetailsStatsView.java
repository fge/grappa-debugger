package com.github.fge.grappa.debugger.tracetab.stat.classdetails;

import com.github.fge.grappa.debugger.stats.classdetails.MatcherClassDetails;
import com.github.fge.grappa.debugger.stats.classdetails.RuleInvocationDetails;
import javafx.scene.chart.PieChart.Data;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@ParametersAreNonnullByDefault
public final class JavafxClassDetailsStatsView
    implements ClassDetailsStatsView
{
    private final ClassDetailsStatsDisplay display;

    private int totalInvocations;

    public JavafxClassDetailsStatsView(final ClassDetailsStatsDisplay display)
    {
        this.display = Objects.requireNonNull(display);
    }

    @Override
    public void loadClassDetails(
        final Map<String, MatcherClassDetails> classDetails)
    {
        Objects.requireNonNull(classDetails);
        final Collection<MatcherClassDetails> values = classDetails.values();
        totalInvocations = values.stream()
            .mapToInt(details -> details.getFailedMatches()
                + details.getNonEmptyMatches() + details.getEmptyMatches()
            ).sum();
        display.classNames.getItems().setAll(values);
    }

    @SuppressWarnings("AutoBoxing")
    @Override
    public void showClassDetails(final MatcherClassDetails details)
    {
        final Set<RuleInvocationDetails> ruleDetails = details.getRuleDetails();

        final int emptyMatches = details.getEmptyMatches();
        final int failedMatches = details.getFailedMatches();
        final int nonEmptyMatches = details.getNonEmptyMatches();
        final int totalMatches = emptyMatches + nonEmptyMatches + failedMatches;

        display.matcherType.setText(details.getMatcherType().toString());
        display.nrRules.setText(String.valueOf(ruleDetails.size()));
        display.nrInvocations.setText(String.valueOf(totalMatches));
        display.invPct.setText(String.format("%.2f%%",
            100.0 * totalMatches / totalInvocations));

        final List<Data> list = new ArrayList<>(3);

        int nr;
        double percent;
        String fmt;

        /*
         * Failures
         */
        nr = failedMatches;
        percent = 100.0 * nr / totalMatches;
        fmt = String.format("Failures (%d - %.02f%%)", nr, percent);
        list.add(new Data(fmt, percent));

        /*
         * Empty
         */
        nr = emptyMatches;
        percent = 100.0 * nr / totalMatches;
        fmt = String.format("Empty matches (%d - %.02f%%)", nr, percent);
        list.add(new Data(fmt, percent));

        /*
         * Non empty
         */
        nr = nonEmptyMatches;
        percent = 100.0 * nr / totalMatches;
        fmt = String.format("Non empty matches (%d; %.02f%%)", nr, percent);
        list.add(new Data(fmt, percent));

        display.pieChart.getData().setAll(list);

        fmt = String.format("Invocations (%d total)", totalMatches);
        display.pieChart.setTitle(fmt);

        display.ruleTable.getItems().setAll(details.getRuleDetails());
    }
}
