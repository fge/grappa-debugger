package com.github.fge.grappa.debugger.csvtrace.tabs.stats;

import com.github.fge.grappa.debugger.common.JavafxDisplay;
import com.github.fge.grappa.debugger.csvtrace.newmodel.RuleInfo;
import com.github.fge.grappa.matchers.MatcherType;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

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
    protected Button tableRefresh;

    @FXML
    protected TableView<RuleInfo> topTen;

    @FXML
    protected TableColumn<RuleInfo, String> ruleName;

    @FXML
    protected TableColumn<RuleInfo, String> ruleClass;

    @FXML
    protected TableColumn<RuleInfo, MatcherType> ruleType;

    @FXML
    protected TableColumn<RuleInfo, Integer> nrCalls;

    @FXML
    protected TableColumn<RuleInfo, String> callDetail;

    @FXML
    protected TableColumn<RuleInfo, RuleInfo> callGraph;

    @Override
    public void init()
    {
        // TODO

    }

    public void refreshTopTen(Event event)
    {
        // TODO

    }
}
