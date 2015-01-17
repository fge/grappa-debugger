package com.github.parboiled1.grappa.debugger.tracetab;

import com.github.parboiled1.grappa.debugger.tracetab.statistics.RuleStatistics;
import com.github.parboiled1.grappa.trace.TraceEvent;
import com.github.parboiled1.grappa.trace.TraceEventType;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class TraceTabUi
{
    private TraceTabPresenter presenter;

    @FXML
    Label nrRules;

    @FXML
    Label parseDate;

    @FXML
    Label totalParseTime;
    /*
     * Statistics table
     */
    @FXML
    TableView<RuleStatistics> stats;

    @FXML
    TableColumn<RuleStatistics, String> statsRule;

    @FXML
    TableColumn<RuleStatistics, Integer> statsInvocations;

    @FXML
    TableColumn<RuleStatistics, Integer> statsSuccess;

    @FXML
    TableColumn<RuleStatistics, Double> statsSuccessRate;

    @FXML
    TableColumn<RuleStatistics, Long> statsTotalTime;

    @FXML
    TableColumn<RuleStatistics, Long> statsAvgTime;

    /*
     * Events tab
     */
    @FXML
    Tab eventsTab;

    /*
     * Events table
     */
    @FXML
    TableView<TraceEvent> events;

    @FXML
    TableColumn<TraceEvent, Long> eventTime;

    @FXML
    TableColumn<TraceEvent, String> eventRule;

    @FXML
    TableColumn<TraceEvent, Integer> eventIndex;

    @FXML
    TableColumn<TraceEvent, TraceEventType> eventType;

    @FXML
    TableColumn<TraceEvent, Integer> eventDepth;

    @FXML
    TableColumn<TraceEvent, String> eventPath;

    public void init(final TraceTabPresenter presenter)
    {
        this.presenter = presenter;
    }
}
