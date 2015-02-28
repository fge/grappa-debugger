package com.github.fge.grappa.debugger.javafx.csvtrace.tabs.rules;

import com.github.fge.grappa.debugger.csvtrace.tabs.rules.RulesTabPresenter;
import com.github.fge.grappa.debugger.csvtrace.tabs.rules.RulesTabView;
import com.github.fge.grappa.debugger.javafx.common.JavafxUtils;
import com.github.fge.grappa.debugger.javafx.common.JavafxView;
import com.github.fge.grappa.debugger.model.ParseInfo;
import com.github.fge.grappa.debugger.model.db.PerClassStatistics;
import com.github.fge.grappa.internal.NonFinalForTesting;
import com.github.fge.grappa.matchers.MatcherType;
import javafx.scene.chart.PieChart;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

@ParametersAreNonnullByDefault
@NonFinalForTesting
public class JavafxRulesTabView
    extends JavafxView<RulesTabPresenter, RulesTabDisplay>
    implements RulesTabView
{
    public JavafxRulesTabView()
        throws IOException
    {
        super("/tabs/rulesTab.fxml");
    }

    @Override
    public void displayParseInfo(final ParseInfo info)
    {
        Objects.requireNonNull(info);

        display.parseDate.setText(info.getTime().toString());

        double ratio;

        final int nrInvocations = info.getNrInvocations();
        display.nrRules.setText(String.valueOf(nrInvocations));

        ratio = (double) nrInvocations / info.getNrLines();
        display.invPerLine.setText(String.format("%.2f", ratio));

        ratio = (double) nrInvocations / info.getNrChars();
        display.invPerChar.setText(String.format("%.2f", ratio));

        display.treeDepth.setText(String.valueOf(info.getTreeDepth()));
    }

    @Override
    public void displayTotalParseTime(final long totalParseTime)
    {
        display.totalParseTime.setText(
            JavafxUtils.nanosToString(totalParseTime));
    }

    @Override
    public void displayMatchersByType(
        final Map<MatcherType, Integer> matchersByType)
    {
        final Function<MatcherType, Integer> zero = t -> 0;
        final int totalMatchers = matchersByType.values().stream()
            .mapToInt(Integer::intValue).sum();

        MatcherType type;
        int nr;
        double pct;
        String fmt;
        PieChart.Data data;

        type = MatcherType.TERMINAL;
        nr = matchersByType.computeIfAbsent(type, zero);
        pct = 100.0 * nr / totalMatchers;
        fmt = String.format("%s (%d; %.02f%%)", type, nr, pct);
        data = display.terminalsPie;
        data.setName(fmt);
        data.setPieValue(nr);

        type = MatcherType.COMPOSITE;
        nr = matchersByType.computeIfAbsent(type, zero);
        pct = 100.0 * nr / totalMatchers;
        fmt = String.format("%s (%d; %.02f%%)", type, nr, pct);
        data = display.compositesPie;
        data.setName(fmt);
        data.setPieValue(nr);

        type = MatcherType.PREDICATE;
        nr = matchersByType.computeIfAbsent(type, zero);
        pct = 100.0 * nr / totalMatchers;
        fmt = String.format("%s (%d; %.02f%%)", type, nr, pct);
        data = display.predicatesPie;
        data.setName(fmt);
        data.setPieValue(nr);

        type = MatcherType.ACTION;
        nr = matchersByType.computeIfAbsent(type, zero);
        pct = 100.0 * nr / totalMatchers;
        fmt = String.format("%s (%d; %.02f%%)", type, nr, pct);
        data = display.actionsPie;
        data.setName(fmt);
        data.setPieValue(nr);

        fmt = String.format("Matchers by type (%d total)", totalMatchers);
        display.rulesChart.setTitle(fmt);
    }

    @Override
    public void displayRules(final List<PerClassStatistics> stats)
    {
        display.rulesTable.getSortOrder().setAll(display.invCount);
        display.rulesTable.getItems().setAll(stats);
        display.rulesTable.sort();
    }
}
