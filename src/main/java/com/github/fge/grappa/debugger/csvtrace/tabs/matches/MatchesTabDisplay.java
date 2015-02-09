package com.github.fge.grappa.debugger.csvtrace.tabs.matches;

import com.github.fge.grappa.debugger.javafx.CallGraphTableCell;
import com.github.fge.grappa.debugger.javafx.JavafxDisplay;
import com.github.fge.grappa.debugger.model.db.MatchStatistics;
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
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
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
    protected ToolBar toolbar;

    @FXML
    public HBox hbox;

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
    protected TableColumn<MatchStatistics, MatchStatistics>
        callGraph;


    @Override
    public void init()
    {
        /*
         * Top toolbar
         */
        hbox.minWidthProperty().bind(toolbar.widthProperty());

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

    @FXML
    void refreshTabEvent(final Event event)
    {
        presenter.handleTabRefresh();
    }
}
