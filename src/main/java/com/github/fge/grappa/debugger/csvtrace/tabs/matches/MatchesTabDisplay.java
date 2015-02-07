package com.github.fge.grappa.debugger.csvtrace.tabs.matches;

import com.github.fge.grappa.debugger.common.JavafxDisplay;
import com.github.fge.grappa.debugger.common.db.RuleInvocationStatistics;
import com.github.fge.grappa.debugger.javafx.CallGraphTableCell;
import com.github.fge.grappa.matchers.MatcherType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
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
    protected BorderPane completionBar;

    @FXML
    protected Button tabRefresh;

    @FXML
    protected ProgressBar loadProgressBar;

    /*
     * General stats
     */
    @FXML
    protected Label nrMatches;

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

    protected PieChart.Data failedMatchesPie = new PieChart.Data("", 0.0);
    protected PieChart.Data emptyMatchesPie = new PieChart.Data("", 0.0);
    protected PieChart.Data nonEmptyMatchesPie = new PieChart.Data("", 0.0);

    /*
     * Invocation table
     */
    @FXML
    protected TableView<RuleInvocationStatistics> matchesTable;

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

        final ObservableList<PieChart.Data> list
            = FXCollections.observableArrayList();

        list.addAll(failedMatchesPie, emptyMatchesPie, nonEmptyMatchesPie);

        invocationsChart.setData(list);

        matchesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    @FXML
    void refreshTabEvent(final Event event)
    {
        presenter.handleTabRefresh();
    }
}
