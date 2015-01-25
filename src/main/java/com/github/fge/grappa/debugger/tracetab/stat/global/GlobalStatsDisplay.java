package com.github.fge.grappa.debugger.tracetab.stat.global;

import com.github.fge.grappa.debugger.internal.NotFXML;
import com.github.fge.grappa.debugger.javafx.JavafxUtils;
import com.github.fge.grappa.debugger.statistics.RuleMatchingStats;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
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
    private TableColumn<RuleMatchingStats, String> statsRule;

    @FXML
    private TableColumn<RuleMatchingStats, Integer> statsInvocations;

    @FXML
    private TableColumn<RuleMatchingStats, Integer> statsSuccess;

    @FXML
    private TableColumn<RuleMatchingStats, Double> statsSuccessRate;

    @FXML
    private TableColumn<RuleMatchingStats, Integer> statsEmptyMatches;

    @FXML
    private TableColumn<RuleMatchingStats, Double> statsEmptyMatchesPct;

    @NotFXML
    public void setPresenter(final GlobalStatsPresenter presenter)
    {
        this.presenter = Objects.requireNonNull(presenter);
        init();
    }

    protected void init()
    {
        JavafxUtils.setColumnValue(statsRule, RuleMatchingStats::getRuleName);
        JavafxUtils.setColumnValue(statsInvocations,
            stats -> stats.getNonEmptyMatches() + stats.getEmptyMatches()
                + stats.getFailures());
        JavafxUtils.setColumnValue(statsSuccess,
            stats -> stats.getEmptyMatches() + stats.getNonEmptyMatches());
        JavafxUtils.setColumnValue(statsSuccessRate,
            stats -> {
                final int successes = stats.getEmptyMatches() + stats
                    .getNonEmptyMatches();
                final int failures = stats.getFailures();

                return 100.0 * successes / (failures + successes);
            });
        statsSuccessRate.setCellFactory(
            param -> new TableCell<RuleMatchingStats, Double>()
            {
                @Override
                protected void updateItem(final Double item,
                    final boolean empty)
                {
                    super.updateItem(item, empty);
                    setText(empty ? null : String.format("%.2f%%", item));
                }
            });
        JavafxUtils.setColumnValue(statsEmptyMatches,
            RuleMatchingStats::getEmptyMatches);
        //Utils.bindColumn(statsEmptyMatches, "emptyMatches");
        statsEmptyMatchesPct.setCellValueFactory(
            param -> new SimpleObjectProperty<Double>()
            {
                @SuppressWarnings("AutoBoxing")
                @Override
                public Double get()
                {
                    final RuleMatchingStats stats = param.getValue();
                    final int nonEmptyMatches = stats.getNonEmptyMatches();
                    final int emptyMatches = stats.getEmptyMatches();
                    final int successfulMatches
                        = nonEmptyMatches + emptyMatches;
                    return successfulMatches == 0 ? -1.0
                        : 100.0 * emptyMatches / successfulMatches;
                }
            }
        );
        statsEmptyMatchesPct.setCellFactory(
            param -> new TableCell<RuleMatchingStats, Double>()
            {
                @SuppressWarnings("AutoBoxing")
                @Override
                protected void updateItem(final Double item,
                    final boolean empty)
                {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                        return;
                    }
                    setText(item.compareTo(0.0) < 0 ? "N/A"
                        : String.format("%.2f%%", item));
                }
            }
        );
    }
}
