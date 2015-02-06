package com.github.fge.grappa.debugger.csvtrace.tabs.matches;

import com.github.fge.grappa.debugger.common.JavafxDisplay;
import com.github.fge.grappa.debugger.common.db.RuleInvocationStatistics;
import com.github.fge.grappa.debugger.javafx.CallGraphTableCell;
import com.github.fge.grappa.matchers.MatcherType;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToolBar;
import javafx.scene.text.Text;

import java.util.function.Function;

import static com.github.fge.grappa.debugger.javafx.JavafxUtils.setColumnValue;

public class MatchesTabDisplay
    extends JavafxDisplay<MatchesTabPresenter>
{
    /*
     * Top bar
     */
    @FXML
    protected ToolBar completionStatus;

    @FXML
    protected Button tabRefresh;

    @FXML
    protected ProgressBar loadProgressBar;

    /*
     * General stats
     */
    @FXML
    protected Label nrInvocations;

    @FXML
    protected Text successRate;

    @FXML
    protected Text topRulePct;

    @FXML
    protected Text topFiveRulePct;

    @FXML
    protected Text topTenRulePct;

    /*
     * Invocation chart
     */
    @FXML
    protected PieChart invocationsChart;

    /*
     * Invocation table
     */
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


    @Override
    public void init()
    {
        setColumnValue(ruleName, r -> r.getRuleInfo().getName());
        setColumnValue(ruleClass, r -> r.getRuleInfo().getClassName());
        setColumnValue(ruleType, r -> r.getRuleInfo().getType());
        setColumnValue(nrCalls, r -> r.getEmptyMatches() + r.getFailedMatches()
            + r.getNonEmptyMatches());
        //noinspection AutoBoxing
        setColumnValue(callDetail, r -> String.format("%d / %d / %d",
            r.getNonEmptyMatches(), r.getEmptyMatches(), r.getFailedMatches()));
        setColumnValue(callGraph, Function.identity());
        callGraph.setCellFactory(CallGraphTableCell::new);
        invocationStatsTable.setColumnResizePolicy(
            TableView.CONSTRAINED_RESIZE_POLICY);
    }

    @FXML
    void refreshStatisticsEvent(final Event event)
    {
        presenter.handleRefreshStatistics();
    }
}
