package com.github.fge.grappa.debugger.tracetab.stat.global;

import com.github.fge.grappa.debugger.statistics.RuleMatchingStats;
import com.github.fge.grappa.debugger.statistics.Utils;
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

    void setPresenter(final GlobalStatsPresenter presenter)
    {
        this.presenter = Objects.requireNonNull(presenter);
        init();
    }

    protected void init()
    {
        Utils.bindColumn(statsRule, "ruleName");
        statsInvocations.setCellValueFactory(
            param -> new SimpleObjectProperty<Integer>()
            {
                @SuppressWarnings("AutoBoxing")
                @Override
                public Integer get()
                {
                    final RuleMatchingStats stats = param.getValue();
                    return stats.getNonEmptyMatches() + stats.getEmptyMatches()
                        + stats.getFailures();
                }
            }
        );
        statsSuccess.setCellValueFactory(
            param -> new SimpleObjectProperty<Integer>()
            {
                @SuppressWarnings("AutoBoxing")
                @Override
                public Integer get()
                {
                    final RuleMatchingStats stats = param.getValue();
                    return stats.getEmptyMatches() + stats.getNonEmptyMatches();
                }
            }
        );
        statsSuccessRate.setCellValueFactory(
            param -> new SimpleObjectProperty<Double>()
            {
                @SuppressWarnings("AutoBoxing")
                @Override
                public Double get()
                {
                    final RuleMatchingStats stats = param.getValue();
                    final int successes = stats.getEmptyMatches()
                        + stats.getNonEmptyMatches();
                    final int failures = stats.getFailures();

                    return 100.0 * successes / (failures + successes);
                }
            }
        );
        statsSuccessRate.setCellFactory(
            param -> new TableCell<RuleMatchingStats, Double>()
            {
                @Override
                protected void updateItem(final Double item,
                    final boolean empty)
                {
                    super.updateItem(item, empty);
                    setText(empty ? null : String.format("%.2f", item));
                }
            }
        );
    }
}
