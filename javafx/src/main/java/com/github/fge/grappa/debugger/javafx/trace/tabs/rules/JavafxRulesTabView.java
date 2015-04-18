package com.github.fge.grappa.debugger.javafx.trace.tabs.rules;

import com.github.fge.grappa.debugger.ParseInfo;
import com.github.fge.grappa.debugger.javafx.common.JavafxUtils;
import com.github.fge.grappa.debugger.javafx.common.JavafxView;
import com.github.fge.grappa.debugger.model.rules.PerClassStatistics;
import com.github.fge.grappa.debugger.trace.tabs.rules.RulesTabPresenter;
import com.github.fge.grappa.debugger.trace.tabs.rules.RulesTabView;
import com.github.fge.grappa.matchers.MatcherType;
import javafx.scene.chart.PieChart;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class JavafxRulesTabView
    extends JavafxView<RulesTabPresenter, RulesTabDisplay>
    implements RulesTabView
{
    public JavafxRulesTabView()
        throws IOException
    {
        super("/javafx/tabs/rules.fxml");
    }

    @Override
    public void displayParseTime(final long nanos)
    {
        display.parseTime.setText(JavafxUtils.nanosToString(nanos));
    }

    @SuppressWarnings({ "AutoBoxing", "AutoUnboxing" })
    @Override
    public void displayPieChart(final Map<MatcherType, Integer> map)
    {
        final Function<MatcherType, Integer> zero = t -> 0;
        final int totalMatchers = map.values().stream()
            .mapToInt(Integer::intValue).sum();

        MatcherType type;
        int nr;
        double pct;
        String fmt;
        PieChart.Data data;

        type = MatcherType.TERMINAL;
        nr = map.computeIfAbsent(type, zero);
        pct = 100.0 * nr / totalMatchers;
        fmt = String.format("%s (%d; %.02f%%)", type, nr, pct);
        data = display.terminalsPie;
        data.setName(fmt);
        data.setPieValue(nr);

        type = MatcherType.COMPOSITE;
        nr = map.computeIfAbsent(type, zero);
        pct = 100.0 * nr / totalMatchers;
        fmt = String.format("%s (%d; %.02f%%)", type, nr, pct);
        data = display.compositesPie;
        data.setName(fmt);
        data.setPieValue(nr);

        type = MatcherType.PREDICATE;
        nr = map.computeIfAbsent(type, zero);
        pct = 100.0 * nr / totalMatchers;
        fmt = String.format("%s (%d; %.02f%%)", type, nr, pct);
        data = display.predicatesPie;
        data.setName(fmt);
        data.setPieValue(nr);

        type = MatcherType.ACTION;
        nr = map.computeIfAbsent(type, zero);
        pct = 100.0 * nr / totalMatchers;
        fmt = String.format("%s (%d; %.02f%%)", type, nr, pct);
        data = display.actionsPie;
        data.setName(fmt);
        data.setPieValue(nr);

        fmt = String.format("Matchers by type (%d total)", totalMatchers);
        display.rulesChart.setTitle(fmt);
    }

    @Override
    public void displayTable(final List<PerClassStatistics> list)
    {
        display.rulesTable.getSortOrder().setAll(display.invCount);
        display.rulesTable.getItems().setAll(list);
        display.rulesTable.sort();
    }

    @SuppressWarnings("AutoBoxing")
    @Override
    public void displayInfo(final ParseInfo info)
    {
        display.parseDate.setText(info.getTime().toString());

        double ratio;

        final int nrNodes = info.getNrNodes();
        display.nrRules.setText(String.valueOf(nrNodes));

        ratio = (double) nrNodes / info.getNrLines();
        display.invPerLine.setText(String.format("%.2f", ratio));

        ratio = (double) nrNodes / info.getNrChars();
        display.invPerChar.setText(String.format("%.2f", ratio));

        display.treeDepth.setText(String.valueOf(info.getTreeDepth()));
    }
}
