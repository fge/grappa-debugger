package com.github.fge.grappa.debugger.tracetab.stat.global;

import com.github.fge.grappa.debugger.internal.NotFXML;
import com.github.fge.grappa.debugger.javafx.JavafxUtils;
import com.github.fge.grappa.debugger.statistics.RuleMatchingStats;
import com.github.fge.grappa.matchers.MatcherType;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@ParametersAreNonnullByDefault
public class GlobalStatsDisplay
{
    @SuppressWarnings("InstanceVariableMayNotBeInitialized")
    private GlobalStatsPresenter presenter;

    /*
     * Text stats
     */
    @FXML
    Label parseDate;

    @FXML
    Label totalParseTime;

    @FXML
    Label treeDepth;

    @FXML
    Label nrRules;

    @FXML
    Label invPerLine;

    @FXML
    Label invPerChar;

    /*
     * Pie chart
     */
    @FXML
    PieChart matchChart;

    /*
     * Table
     */
    @FXML
    TableView<RuleMatchingStats> stats;

    @FXML
    private TableColumn<RuleMatchingStats, String> ruleName;

    @FXML
    private TableColumn<RuleMatchingStats, String> ruleClass;

    @FXML
    private TableColumn<RuleMatchingStats, MatcherType> ruleType;

    @FXML
    private TableColumn<RuleMatchingStats, Integer> nrCalls;

    @FXML
    private TableColumn<RuleMatchingStats, String> callDetail;

    @FXML
    private TableColumn<RuleMatchingStats, String> callDetailPct;

    @NotFXML
    public void setPresenter(final GlobalStatsPresenter presenter)
    {
        this.presenter = Objects.requireNonNull(presenter);
        init();
    }

    protected void init()
    {
        /*
         * Stats table
         */
        JavafxUtils.setColumnValue(ruleName, RuleMatchingStats::getRuleName);

        JavafxUtils.setColumnValue(ruleClass,
            RuleMatchingStats::getMatcherClass);

        JavafxUtils.setColumnValue(ruleType, RuleMatchingStats::getMatcherType);

        JavafxUtils.setColumnValue(nrCalls,
            stats -> stats.getNonEmptyMatches() + stats.getEmptyMatches()
                + stats.getFailures());

        //noinspection AutoBoxing
        JavafxUtils.setColumnValue(callDetail,
            stats -> String.format("%d/%d/%d", stats.getNonEmptyMatches(),
                stats.getEmptyMatches(), stats.getFailures()));

        JavafxUtils.setColumnValue(callDetailPct, stats -> {
            final int nonEmpty = stats.getNonEmptyMatches();
            final int empty = stats.getEmptyMatches();
            final int failures = stats.getFailures();
            final int total = nonEmpty + empty + failures;
            return String.format("%.2f%%/%.2f%%/%.2f%%",
                100.0 * nonEmpty / total, 100.0 * empty / total,
                100.0 * failures / total);
        });
    }
}
