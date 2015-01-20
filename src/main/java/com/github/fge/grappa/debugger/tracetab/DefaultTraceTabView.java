package com.github.fge.grappa.debugger.tracetab;

import com.github.fge.grappa.debugger.tracetab.statistics.InputTextInfo;
import com.github.fge.grappa.debugger.tracetab.statistics.ParseNode;
import com.github.fge.grappa.debugger.tracetab.statistics.RuleStatistics;
import com.github.fge.grappa.debugger.tracetab.statistics.Utils;
import com.github.parboiled1.grappa.buffers.InputBuffer;
import com.github.parboiled1.grappa.trace.TraceEvent;
import com.github.parboiled1.grappa.trace.TraceEventType;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.parboiled.support.Position;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class DefaultTraceTabView
    implements TraceTabView
{
    private final TraceTabUi ui;

    private int nrLines;

    public DefaultTraceTabView(final TraceTabUi ui)
    {
        this.ui = ui;

        /*
         * Parse tree
         */
        ui.parseTree.setCellFactory(param -> new ParseNodeCell(ui));

        /*
         * Trace events
         */
        bindColumn(ui.eventTime, "nanoseconds");
        setDisplayNanos(ui.eventTime);
        bindColumn(ui.eventDepth, "level");
        bindColumn(ui.eventIndex, "index");
        bindColumn(ui.eventPath, "path");
        bindColumn(ui.eventRule, "matcher");
        bindColumn(ui.eventType, "type");
        ui.eventType.setCellFactory(
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

        /*
         * Statistics
         */
        bindColumn(ui.statsRule, "ruleName");
        bindColumn(ui.statsInvocations, "nrInvocations");
        bindColumn(ui.statsSuccess, "nrSuccesses");
        ui.statsSuccessRate.setCellValueFactory(
            param -> new SimpleObjectProperty<Double>()
            {
                @SuppressWarnings("AutoBoxing")
                @Override
                public Double get()
                {
                    final RuleStatistics stats = param.getValue();
                    //noinspection AutoBoxing
                    return 100.0 * stats.getNrSuccesses()
                        / stats.getNrInvocations();
                }
            });
        ui.statsSuccessRate.setCellFactory(
            param -> new TableCell<RuleStatistics, Double>()
            {
                @Override
                protected void updateItem(final Double item,
                    final boolean empty)
                {
                    super.updateItem(item, empty);
                    setText(empty ? null : String.format("%.2f%%", item));
                }
            });
    }

    @Override
    public void setTraceEvents(final List<TraceEvent> events)
    {
        ui.events.getItems().setAll(events);
        final Tab tab = ui.eventsTab;
        final int size = events.size();
        @SuppressWarnings("AutoBoxing")
        final String newText = String.format("%s (%d)", tab.getText(), size);

        tab.setText(newText);

        final long nanos = events.get(size - 1).getNanoseconds();
        ui.totalParseTime.setText(Utils.nanosToString(nanos));
    }

    @Override
    public void setStatistics(final Collection<RuleStatistics> values)
    {
        ui.nrRules.setText(String.valueOf(values.size()));
        ui.stats.getItems().setAll(values);

        int totalInvocations = 0;
        int totalSuccesses = 0;

        for (final RuleStatistics stats: values) {
            totalInvocations += stats.getNrInvocations();
            totalSuccesses += stats.getNrSuccesses();
        }

        final double pct = 100.0 * totalSuccesses / totalInvocations;
        ui.totalInvocations.setText(String.valueOf(totalInvocations));
        ui.totalSuccess.setText(String.valueOf(totalSuccesses));
        ui.totalSuccessRate.setText(String.format("%.02f%%", pct));
    }

    @Override
    public void setParseDate(final long startDate)
    {
        final Instant instant = Instant.ofEpochMilli(startDate);
        // TODO: record tz info in the JSON
        final LocalDateTime time
            = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        ui.parseDate.setText(time.toString());
    }

    @Override
    public void setInputTextInfo(final InputTextInfo textInfo)
    {
        nrLines = textInfo.getNrLines();
        ui.textInfo.setText("Input text: " + nrLines  + " lines, "
            + textInfo.getNrChars() + " characters, "
            + textInfo.getNrCodePoints() + " code points");
    }

    @Override
    public void setInputText(final String inputText)
    {
        ui.inputText.getChildren().setAll(new Text(inputText));
    }

    @Override
    public void setParseTree(final ParseNode node)
    {
        ui.parseTree.setRoot(buildTree(node));
    }

    @Override
    public void setParseNodeDetails(final String text)
    {
        ui.parseNodeDetails.setText(text);
    }

    @SuppressWarnings("AutoBoxing")
    @Override
    public void fillParseNodeDetails(final ParseNode node,
        final InputBuffer buffer)
    {
        final boolean success = node.isSuccess();
        Position position;

        ui.parseNodeLevel.setText(String.valueOf(node.getLevel()));

        ui.parseNodeRuleName.setText(node.getRuleName());

        if (success) {
            ui.parseNodeStatus.setText("SUCCESS");
            ui.parseNodeStatus.setTextFill(Color.GREEN);
        } else {
            ui.parseNodeStatus.setText("FAILURE");
            ui.parseNodeStatus.setTextFill(Color.RED);
        }

        position = buffer.getPosition(node.getStart());
        ui.parseNodeStart.setText(String.format("line %d, column %d",
            position.getLine(), position.getColumn()));
        position = buffer.getPosition(node.getEnd());
        ui.parseNodeEnd.setText(String.format("line %d, column %d",
            position.getLine(), position.getColumn()));

        ui.parseNodeTime.setText(Utils.nanosToString(node.getNanos()));
    }

    @Override
    public void highlightText(final List<String> fragments,
        final Position position, final boolean success)
    {
        final TextFlow inputText = ui.inputText;
        final List<Text> list = new ArrayList<>(3);

        Text text;
        String fragment;

        text = (Text) inputText.getChildren().get(0);
        final double lineHeight = text.getFont().getSize()
            + text.getLineSpacing() + inputText.getLineSpacing();

        // Before match
        fragment = fragments.get(0);
        if (!fragment.isEmpty()) {
            text = new Text(fragment);
            text.setFill(Color.GRAY);
            list.add(text);
        }

        // Match
        fragment = fragments.get(1);
        // NOTE: cannot happen
        if (!fragment.isEmpty()) {
            text = new Text(fragment);
            text.setFill(success ? Color.GREEN : Color.RED);
            text.setUnderline(true);
            list.add(text);
        }

        // After match
        fragment = fragments.get(2);
        if (!fragment.isEmpty()) {
            text = new Text(fragment);
            list.add(text);
        }

        inputText.getChildren().setAll(list);

        final ScrollPane scroll = ui.inputTextScroll;
        double line = position.getLine();
        if (line != nrLines)
            line--;
        scroll.setVvalue(line / nrLines);
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
        private ParseNodeCell(final TraceTabUi ui)
        {
            setEditable(false);
            setOnMouseClicked(event -> {
                final ParseNode node = getItem();
                ui.parseNodeShowEvent(node);
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

    private static TreeItem<ParseNode> buildTree(final ParseNode root)
    {
        final TreeItem<ParseNode> ret = new TreeItem<>(root);

        addChildren(ret, root);

        return ret;
    }

    private static void addChildren(final TreeItem<ParseNode> item,
        final ParseNode parent)
    {
        TreeItem<ParseNode> childItem;
        final List<TreeItem<ParseNode>> childrenItems
            = FXCollections.observableArrayList();

        for (final ParseNode node: parent.getChildren()) {
            childItem = new TreeItem<>(node);
            addChildren(childItem, node);
            childrenItems.add(childItem);
        }

        item.getChildren().setAll(childrenItems);
    }
}
