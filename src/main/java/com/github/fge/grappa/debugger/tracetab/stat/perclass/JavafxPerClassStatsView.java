package com.github.fge.grappa.debugger.tracetab.stat.perclass;

import javafx.scene.chart.PieChart.Data;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

@ParametersAreNonnullByDefault
public final class JavafxPerClassStatsView
    implements PerClassStatsView
{
    private final PerClassStatsDisplay display;

    public JavafxPerClassStatsView(final PerClassStatsDisplay display)
    {
        this.display = Objects.requireNonNull(display);
    }

    @Override
    public void loadPerClass(final Map<String, Long> perClass)
    {
        double total = 0;

        for (final Long value: perClass.values())
            total += value.doubleValue();

        String fmt;
        Long count;
        double countAsDouble;
        double pct;
        Data data;

        for (final Entry<String, Long> entry: perClass.entrySet()) {
            count = entry.getValue();
            countAsDouble = count.doubleValue();
            pct = 100.0 * countAsDouble / total;
            //noinspection AutoBoxing
            fmt = String.format("%s (%d; %.2f%%)", entry.getKey(), count, pct);
            data = new Data(fmt, countAsDouble);
            display.ruleChart.getData().add(data);
        }
    }
}
