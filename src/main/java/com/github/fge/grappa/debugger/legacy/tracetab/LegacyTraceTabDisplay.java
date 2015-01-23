package com.github.fge.grappa.debugger.legacy.tracetab;

import com.github.fge.grappa.debugger.internal.NotFXML;
import com.github.fge.grappa.debugger.legacy.LegacyTraceEvent;
import com.github.fge.grappa.debugger.legacy.RuleStatistics;
import com.github.fge.grappa.debugger.statistics.ParseNode;
import com.github.fge.grappa.trace.TraceEventType;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeView;
import javafx.scene.text.TextFlow;

public class LegacyTraceTabDisplay
{
    private LegacyTraceTabPresenter presenter;

    /*
     * Tree tab
     */
    @FXML
    Button treeExpand;

    @FXML
    TreeView<ParseNode> parseTree;

    @FXML
    Label parseNodeLevel;

    @FXML
    Label parseNodeRuleName;

    @FXML
    Label parseNodeStatus;

    @FXML
    Label parseNodeStart;

    @FXML
    Label parseNodeEnd;

    @FXML
    Label parseNodeTime;

    @FXML
    Label textInfo;

    @FXML
    TextFlow inputText;

    @FXML
    ScrollPane inputTextScroll;

    /*
     * General statistics
     */
    @FXML
    Label parseDate;

    @FXML
    Label nrRules;

    @FXML
    Label totalInvocations;

    @FXML
    Label totalSuccess;

    @FXML
    Label totalSuccessRate;

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

    /*
     * Events tab
     */
    @FXML
    Tab eventsTab;

    /*
     * Events table
     */
    @FXML
    TableView<LegacyTraceEvent> events;

    @FXML
    TableColumn<LegacyTraceEvent, Long> eventTime;

    @FXML
    TableColumn<LegacyTraceEvent, String> eventRule;

    @FXML
    TableColumn<LegacyTraceEvent, Integer> eventIndex;

    @FXML
    TableColumn<LegacyTraceEvent, TraceEventType> eventType;

    @FXML
    TableColumn<LegacyTraceEvent, Integer> eventDepth;

    @FXML
    TableColumn<LegacyTraceEvent, String> eventPath;

    public void init(final LegacyTraceTabPresenter presenter)
    {
        this.presenter = presenter;
    }

    @FXML
    void expandParseTreeEvent(final Event event)
    {
        presenter.handleExpandParseTree();
    }

    @NotFXML
    void parseNodeShowEvent(final ParseNode node)
    {
        presenter.handleParseNodeShow(node);
    }
}
