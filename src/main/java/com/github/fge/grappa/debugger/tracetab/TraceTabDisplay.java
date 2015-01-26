package com.github.fge.grappa.debugger.tracetab;

import com.github.fge.grappa.debugger.internal.NotFXML;
import com.github.fge.grappa.debugger.javafx.Utils;
import com.github.fge.grappa.debugger.stats.ParseNode;
import com.github.fge.grappa.debugger.stats.StatsType;
import com.github.fge.grappa.trace.TraceEvent;
import com.github.fge.grappa.trace.TraceEventType;
import com.google.common.annotations.VisibleForTesting;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.Objects;

public class TraceTabDisplay
{
    @NotFXML
    @VisibleForTesting
    TraceTabPresenter presenter;

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
     * Stats tabs
     */
    @FXML
    Tab globalStatsTab;

    @FXML
    public Tab classStatsTab;
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

    public void setPresenter(final TraceTabPresenter presenter)
    {
        this.presenter = Objects.requireNonNull(presenter);
        init();
    }

    @VisibleForTesting
    void init()
    {
        /*
         * Parse tree
         */
        parseTree.setCellFactory(param -> new ParseNodeCell(this));

        /*
         * Trace events
         */
        bindColumn(eventTime, "nanoseconds");
        setDisplayNanos(eventTime);
        bindColumn(eventDepth, "level");
        bindColumn(eventIndex, "index");
        bindColumn(eventPath, "path");
        bindColumn(eventRule, "matcher");
        bindColumn(eventType, "type");
        eventType.setCellFactory(
            param -> new TableCell<TraceEvent, TraceEventType>()
            {
                @Override
                protected void updateItem(final TraceEventType item,
                    final boolean empty)
                {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                        return;
                    }
                    final Text text = new Text(item.name());
                    switch (item) {
                        case MATCH_FAILURE:
                            text.setFill(Color.RED);
                            break;
                        case MATCH_SUCCESS:
                            text.setFill(Color.GREEN);
                    }
                    setGraphic(text);
                }
            });
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

    @NotFXML
    void loadStatsEvent(final StatsType statsType)
    {
        presenter.handleLoadStats(statsType);
    }

    private static <S, T> void bindColumn(final TableColumn<S, T> column,
        final String propertyName)
    {
        column.setCellValueFactory(new PropertyValueFactory<>(propertyName));
    }

    private static <S> void setDisplayNanos(final TableColumn<S, Long> column)
    {
        column.setCellFactory(param -> new TableCell<S, Long>()
        {
            @Override
            protected void updateItem(final Long item, final boolean empty)
            {
                super.updateItem(item, empty);
                //noinspection AutoUnboxing
                setText(empty ? null : Utils.nanosToString(item));
            }
        });
    }

    private static final class ParseNodeCell
        extends TreeCell<ParseNode>
    {
        private ParseNodeCell(final TraceTabDisplay display)
        {
            setEditable(false);
            selectedProperty().addListener(new ChangeListener<Boolean>()
            {
                @Override
                public void changed(
                    final ObservableValue<? extends Boolean> observable,
                    final Boolean oldValue, final Boolean newValue)
                {
                    if (!newValue)
                        return;
                    final ParseNode node = getItem();
                    if (node != null)
                        display.parseNodeShowEvent(node);
                }
            });
        }

        @Override
        protected void updateItem(final ParseNode item, final boolean empty)
        {
            super.updateItem(item, empty);
            setText(empty ? null : String.format("%s (%s)", item.getRuleName(),
                item.isSuccess() ? "SUCCESS" : "FAILURE"));
        }
    }
}
