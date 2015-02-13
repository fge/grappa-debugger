package com.github.fge.grappa.debugger.csvtrace.tabs.matches;

import com.github.fge.grappa.debugger.javafx.JavafxView;
import com.github.fge.grappa.debugger.model.db.MatchStatistics;
import javafx.scene.chart.PieChart;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.util.List;

@ParametersAreNonnullByDefault
public final class JavafxMatchesTabView
    extends JavafxView<MatchesTabPresenter, MatchesTabDisplay>
    implements MatchesTabView
{
    public JavafxMatchesTabView()
        throws IOException
    {
        super("/tabs/matchesTab.fxml");
    }

    @Override
    public void showMatches(final List<MatchStatistics> stats)
    {
        display.matchesTable.getSortOrder().setAll(display.nrCalls);
        display.matchesTable.getItems().setAll(stats);
        display.matchesTable.sort();
    }

    @Override
    public void showMatchesStats(final int nonEmpty, final int empty,
        final int failures)
    {
        final int success = nonEmpty + empty;
        final int total = success + failures;

        display.nrMatches.setText(String.valueOf(total));

        final double successPct = 100.0 * success / total;
        Color color = Color.RED;
        if (Double.compare(successPct, 25.0) > 0)
            color = Color.ORANGE;
        if (Double.compare(successPct, 40.0) > 0)
            color = Color.BLACK;

        display.successRate.setText(String.format("%.02f%%", successPct));
        display.successRate.setFill(color);

        int nr;
        String type;
        double pct;
        PieChart.Data data;
        String fmt;

        nr = failures;
        type = "Failed matches";
        pct = 100.0 * nr / total;
        fmt = String.format("%s (%d; %.02f%%)", type, nr, pct);
        data = display.failedPie;
        data.setName(fmt);
        data.setPieValue((double) nr);

        nr = empty;
        type = "Empty matches";
        pct = 100.0 * nr / total;
        fmt = String.format("%s (%d; %.02f%%)", type, nr, pct);
        data = display.emptyPie;
        data.setName(fmt);
        data.setPieValue((double) nr);

        nr = nonEmpty;
        type = "Non empty matches";
        pct = 100.0 * nr / total;
        fmt = String.format("%s (%d; %.02f%%)", type, nr, pct);
        data = display.nonEmptyPie;
        data.setName(fmt);
        data.setPieValue((double) nr);

        fmt = String.format("All matches (%d total)", total);
        display.invocationsChart.setTitle(fmt);
    }

    @Override
    public void showTopOne(@Nullable final Integer topOne, final int total)
    {
        doShowTopN(display.topRulePct, topOne, total);
    }

    @Override
    public void showTopFive(@Nullable final Integer topFive, final int total)
    {
        doShowTopN(display.topFiveRulePct, topFive, total);
    }

    @Override
    public void showTopTen(@Nullable final Integer topTen, final int total)
    {
        doShowTopN(display.topTenRulePct, topTen, total);
    }

    private void doShowTopN(final Text text, @Nullable final Integer integer,
        final int total)
    {
        if (integer == null) {
            text.setText("N/A");
            return;
        }

        final double pct = 100.0 * integer / total;
        final String fmt = String.format("%.02f%% of all invocations", pct);
        text.setText(fmt);
    }
}
