package com.github.fge.grappa.debugger.javafx.csvtrace.tabs.matches;

import com.github.fge.grappa.debugger.csvtrace.tabs.matches.MatchesTabPresenter;
import com.github.fge.grappa.debugger.javafx.common.JavafxDisplay;
import com.github.fge.grappa.debugger.javafx.custom.CallGraphTableCell;
import com.github.fge.grappa.debugger.model.db.MatchStatistics;
import com.github.fge.grappa.matchers.MatcherType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.text.Text;

import java.util.function.Function;

import static com.github.fge.grappa.debugger.javafx.common.JavafxUtils
    .setColumnValue;

public class MatchesTabDisplay
    extends JavafxDisplay<MatchesTabPresenter>
{
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

    protected final PieChart.Data failedPie = new PieChart.Data("", 0.0);
    protected final PieChart.Data emptyPie = new PieChart.Data("", 0.0);
    protected final PieChart.Data nonEmptyPie = new PieChart.Data("", 0.0);

    /*
     * Invocation table
     */
    @FXML
    protected TableView<MatchStatistics> matchesTable;

    @FXML
    protected TableColumn<MatchStatistics, String> ruleName;

    @FXML
    protected TableColumn<MatchStatistics, String> ruleClass;

    @FXML
    protected TableColumn<MatchStatistics, MatcherType> ruleType;

    @FXML
    protected TableColumn<MatchStatistics, Integer> nrCalls;

    @FXML
    protected TableColumn<MatchStatistics, String> callDetail;

    @FXML
    protected TableColumn<MatchStatistics, MatchStatistics> callGraph;


    @Override
    public void init()
    {
        /*
         * Rules table
         */
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

        list.addAll(failedPie, emptyPie, nonEmptyPie);

        invocationsChart.setData(list);

        matchesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }
}
