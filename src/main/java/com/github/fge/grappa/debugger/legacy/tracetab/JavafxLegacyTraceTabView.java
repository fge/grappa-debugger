package com.github.fge.grappa.debugger.legacy.tracetab;

import com.github.fge.grappa.buffers.InputBuffer;
import com.github.fge.grappa.debugger.legacy.InputTextInfo;
import com.github.fge.grappa.debugger.legacy.LegacyTraceEvent;
import com.github.fge.grappa.debugger.legacy.RuleStatistics;
import com.github.fge.grappa.debugger.statistics.ParseNode;
import com.github.fge.grappa.debugger.statistics.Utils;
import com.github.fge.grappa.trace.TraceEventType;
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

public final class JavafxLegacyTraceTabView
    implements LegacyTraceTabView
{
    private final LegacyTraceTabDisplay display;

    private int nrLines;

    public JavafxLegacyTraceTabView(final LegacyTraceTabDisplay display)
    {
        this.display = display;

        /*
         * Parse tree
         */
        display.parseTree.setCellFactory(param -> new ParseNodeCell(display));

        /*
         * Trace events
         */
        bindColumn(display.eventTime, "nanoseconds");
        setDisplayNanos(display.eventTime);
        bindColumn(display.eventDepth, "level");
        bindColumn(display.eventIndex, "index");
        bindColumn(display.eventPath, "path");
        bindColumn(display.eventRule, "matcher");
        bindColumn(display.eventType, "type");
        display.eventType.setCellFactory(
            param -> new TableCell<LegacyTraceEvent, TraceEventType>()
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
        bindColumn(display.statsRule, "ruleName");
        bindColumn(display.statsInvocations, "nrInvocations");
        bindColumn(display.statsSuccess, "nrSuccesses");
        display.statsSuccessRate.setCellValueFactory(
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
        display.statsSuccessRate.setCellFactory(
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
    public void setTraceEvents(final List<LegacyTraceEvent> events)
    {
        display.events.getItems().setAll(events);
        final Tab tab = display.eventsTab;
        final int size = events.size();
        @SuppressWarnings("AutoBoxing")
        final String newText = String.format("%s (%d)", tab.getText(), size);

        tab.setText(newText);

        final long nanos = events.get(size - 1).getNanoseconds();
        display.totalParseTime.setText(Utils.nanosToString(nanos));
    }

    @Override
    public void setStatistics(final Collection<RuleStatistics> values)
    {
        display.nrRules.setText(String.valueOf(values.size()));
        display.stats.getItems().setAll(values);

        int totalInvocations = 0;
        int totalSuccesses = 0;

        for (final RuleStatistics stats: values) {
            totalInvocations += stats.getNrInvocations();
            totalSuccesses += stats.getNrSuccesses();
        }

        final double pct = 100.0 * totalSuccesses / totalInvocations;
        display.totalInvocations.setText(String.valueOf(totalInvocations));
        display.totalSuccess.setText(String.valueOf(totalSuccesses));
        display.totalSuccessRate.setText(String.format("%.02f%%", pct));
    }

    @Override
    public void setParseDate(final long startDate)
    {
        final Instant instant = Instant.ofEpochMilli(startDate);
        // TODO: record tz info in the JSON
        final LocalDateTime time
            = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        display.parseDate.setText(time.toString());
    }

    @Override
    public void setInputTextInfo(final InputTextInfo textInfo)
    {
        nrLines = textInfo.getNrLines();
        display.textInfo.setText(
            "Input text: " + nrLines + " lines, " + textInfo.getNrChars()
                + " characters, " + textInfo.getNrCodePoints()
                + " code points");
    }

    @Override
    public void setInputText(final String inputText)
    {
        display.inputText.getChildren().setAll(new Text(inputText));
    }

    @Override
    public void setParseTree(final ParseNode node)
    {
        display.parseTree.setRoot(buildTree(node));
    }

    @SuppressWarnings("AutoBoxing")
    @Override
    public void fillParseNodeDetails(final ParseNode node,
        final InputBuffer buffer)
    {
        final boolean success = node.isSuccess();
        Position position;

        display.parseNodeLevel.setText(String.valueOf(node.getLevel()));

        display.parseNodeRuleName.setText(node.getRuleName());

        if (success) {
            display.parseNodeStatus.setText("SUCCESS");
            display.parseNodeStatus.setTextFill(Color.GREEN);
        } else {
            display.parseNodeStatus.setText("FAILURE");
            display.parseNodeStatus.setTextFill(Color.RED);
        }

        position = buffer.getPosition(node.getStart());
        display.parseNodeStart.setText(String.format("line %d, column %d",
            position.getLine(), position.getColumn()));
        position = buffer.getPosition(node.getEnd());
        display.parseNodeEnd.setText(String.format("line %d, column %d",
            position.getLine(), position.getColumn()));

        display.parseNodeTime.setText(Utils.nanosToString(node.getNanos()));
    }

    @Override
    public void highlightText(final List<String> fragments,
        final Position position, final boolean success)
    {
        final TextFlow inputText = display.inputText;
        final List<Text> list = new ArrayList<>(3);

        Text text;
        String fragment;

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

        final ScrollPane scroll = display.inputTextScroll;
        double line = position.getLine();
        if (line != nrLines)
            line--;
        scroll.setVvalue(line / nrLines);
    }

    @Override
    public void expandParseTree()
    {
        final TreeItem<ParseNode> root = display.parseTree.getRoot();

        doExpand(root);
    }

    private static void doExpand(final TreeItem<ParseNode> item)
    {
        item.getChildren().forEach(JavafxLegacyTraceTabView::doExpand);
        item.setExpanded(true);
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
        private ParseNodeCell(final LegacyTraceTabDisplay ui)
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
