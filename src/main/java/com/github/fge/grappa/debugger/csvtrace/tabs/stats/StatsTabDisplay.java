package com.github.fge.grappa.debugger.csvtrace.tabs.stats;

import com.github.fge.grappa.debugger.common.JavafxDisplay;
import com.github.fge.grappa.debugger.common.db.RuleInvocationStatistics;
import com.github.fge.grappa.debugger.javafx.CallGraphTableCell;
import com.github.fge.grappa.matchers.MatcherType;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import static com.github.fge.grappa.debugger.javafx.JavafxUtils.setColumnValue;

public class StatsTabDisplay
    extends JavafxDisplay<StatsTabPresenter>
{
    /*
     * General match information
     */
    @FXML
    protected Label parseDate;

    @FXML
    protected Label totalParseTime;

    @FXML
    protected Label treeDepth;

    @FXML
    protected Label nrRules;

    @FXML
    protected Label invPerLine;

    @FXML
    protected Label invPerChar;

    @FXML
    protected PieChart matchersChart;

    /*
     * Top 10 table
     */
    @FXML
    protected Label completionStatus;

    @FXML
    protected Button tableRefresh;

    @FXML
    protected TableView<RuleInvocationStatistics> invocationStatsTable;

    @FXML
    protected TableColumn<RuleInvocationStatistics, String> ruleName;

    @FXML
    protected TableColumn<RuleInvocationStatistics, String> ruleClass;

    @FXML
    protected TableColumn<RuleInvocationStatistics, MatcherType> ruleType;

    @FXML
    protected TableColumn<RuleInvocationStatistics, Integer> nrCalls;

    @FXML
    protected TableColumn<RuleInvocationStatistics, String> callDetail;

    @FXML
    protected TableColumn<RuleInvocationStatistics, RuleInvocationStatistics>
        callGraph;

    @SuppressWarnings("AutoBoxing")
    @Override
    public void init()
    {
        setColumnValue(ruleName, r -> r.getRuleInfo().getName());
        setColumnValue(ruleClass, r -> r.getRuleInfo().getClassName());
        setColumnValue(ruleType, r -> r.getRuleInfo().getType());
        setColumnValue(nrCalls, r -> r.getEmptyMatches() + r.getFailedMatches()
            + r.getNonEmptyMatches());
        invocationStatsTable.getSortOrder().add(nrCalls);
        setColumnValue(callDetail, r -> String.format("%d / %d / %d",
            r.getNonEmptyMatches(), r.getEmptyMatches(), r.getFailedMatches()));
        callGraph.setCellValueFactory(param ->
            new SimpleObjectProperty<RuleInvocationStatistics>()
            {
                @Override
                public RuleInvocationStatistics get()
                {
                    return param.getValue();
                }
            }
        );
        callGraph.setCellFactory(CallGraphTableCell::new);
    }

    public void refreshInvocationStatistics(final Event event)
    {
        presenter.handleRefreshInvocationStatistics();
    }
}
