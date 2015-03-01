package com.github.fge.grappa.debugger.javafx.csvtrace.tabs.matches;

import com.github.fge.grappa.debugger.csvtrace.tabs.matches.MatchesTabPresenter;
import com.github.fge.grappa.debugger.csvtrace.tabs.matches.MatchesTabView;
import com.github.fge.grappa.debugger.javafx.common.JavafxView;
import com.github.fge.grappa.debugger.model.db.MatchStatistics;
import com.github.fge.grappa.debugger.model.tabs.matches.MatchesData;
import com.github.fge.grappa.internal.NonFinalForTesting;
import com.google.common.annotations.VisibleForTesting;
import javafx.scene.chart.PieChart;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.util.List;

@ParametersAreNonnullByDefault
@NonFinalForTesting
public class JavafxMatchesTabView
    extends JavafxView<MatchesTabPresenter, MatchesTabDisplay>
    implements MatchesTabView
{
    public JavafxMatchesTabView()
        throws IOException
    {
        super("/tabs/matchesTab.fxml");
    }

    @Override
    public void displayMatchesData(final MatchesData data)
    {
        showMatches(data.getAllStats());
        showMatchesStats(data.getNonEmptyMatches(), data.getEmptyMatches(),
            data.getFailedMatches());

        showTopN(display.topRulePct, data.getTopOne());
        showTopN(display.topFiveRulePct, data.getTopFive());
        showTopN(display.topTenRulePct, data.getTopTen());
    }

    @SuppressWarnings("TypeMayBeWeakened")
    @VisibleForTesting
    void showMatches(final List<MatchStatistics> stats)
    {
        display.matchesTable.getSortOrder().setAll(display.nrCalls);
        display.matchesTable.getItems().setAll(stats);
        display.matchesTable.sort();
    }

    private void showMatchesStats(final int nonEmpty, final int empty,
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

    private void showTopN(final Text text, @Nullable final Double pct)
    {
        final String message = pct == null ? "N/A"
            : String.format("%.02f%% of all invocations", pct);
        text.setText(message);
    }
}
