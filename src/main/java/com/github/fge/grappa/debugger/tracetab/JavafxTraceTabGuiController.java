package com.github.fge.grappa.debugger.tracetab;

import com.github.fge.grappa.buffers.InputBuffer;
import com.github.fge.grappa.debugger.legacy.InputTextInfo;
import com.github.fge.grappa.debugger.legacy.RuleStatistics;
import com.github.fge.grappa.debugger.legacy.TraceEvent;
import com.github.fge.grappa.debugger.tracetab.statistics.ParseNode;
import com.github.fge.grappa.debugger.tracetab.statistics.Utils;
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

public final class JavafxTraceTabGuiController
    implements TraceTabGuiController
{
    private final TraceTabGui gui;

    private int nrLines;

    public JavafxTraceTabGuiController(final TraceTabGui gui)
    {
        this.gui = gui;

        /*
         * Parse tree
         */
        gui.parseTree.setCellFactory(param -> new ParseNodeCell(gui));

        /*
         * Trace events
         */
        bindColumn(gui.eventTime, "nanoseconds");
        setDisplayNanos(gui.eventTime);
        bindColumn(gui.eventDepth, "level");
        bindColumn(gui.eventIndex, "index");
        bindColumn(gui.eventPath, "path");
        bindColumn(gui.eventRule, "matcher");
        bindColumn(gui.eventType, "type");
        gui.eventType.setCellFactory(
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
        bindColumn(gui.statsRule, "ruleName");
        bindColumn(gui.statsInvocations, "nrInvocations");
        bindColumn(gui.statsSuccess, "nrSuccesses");
        gui.statsSuccessRate.setCellValueFactory(
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
        gui.statsSuccessRate.setCellFactory(
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
        gui.events.getItems().setAll(events);
        final Tab tab = gui.eventsTab;
        final int size = events.size();
        @SuppressWarnings("AutoBoxing")
        final String newText = String.format("%s (%d)", tab.getText(), size);

        tab.setText(newText);

        final long nanos = events.get(size - 1).getNanoseconds();
        gui.totalParseTime.setText(Utils.nanosToString(nanos));
    }

    @Override
    public void setStatistics(final Collection<RuleStatistics> values)
    {
        gui.nrRules.setText(String.valueOf(values.size()));
        gui.stats.getItems().setAll(values);

        int totalInvocations = 0;
        int totalSuccesses = 0;

        for (final RuleStatistics stats: values) {
            totalInvocations += stats.getNrInvocations();
            totalSuccesses += stats.getNrSuccesses();
        }

        final double pct = 100.0 * totalSuccesses / totalInvocations;
        gui.totalInvocations.setText(String.valueOf(totalInvocations));
        gui.totalSuccess.setText(String.valueOf(totalSuccesses));
        gui.totalSuccessRate.setText(String.format("%.02f%%", pct));
    }

    @Override
    public void setParseDate(final long startDate)
    {
        final Instant instant = Instant.ofEpochMilli(startDate);
        // TODO: record tz info in the JSON
        final LocalDateTime time
            = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        gui.parseDate.setText(time.toString());
    }

    @Override
    public void setInputTextInfo(final InputTextInfo textInfo)
    {
        nrLines = textInfo.getNrLines();
        gui.textInfo.setText(
            "Input text: " + nrLines + " lines, " + textInfo.getNrChars()
                + " characters, " + textInfo.getNrCodePoints()
                + " code points");
    }

    @Override
    public void setInputText(final String inputText)
    {
        gui.inputText.getChildren().setAll(new Text(inputText));
    }

    @Override
    public void setParseTree(final ParseNode node)
    {
        gui.parseTree.setRoot(buildTree(node));
    }

    @SuppressWarnings("AutoBoxing")
    @Override
    public void fillParseNodeDetails(final ParseNode node,
        final InputBuffer buffer)
    {
        final boolean success = node.isSuccess();
        Position position;

        gui.parseNodeLevel.setText(String.valueOf(node.getLevel()));

        gui.parseNodeRuleName.setText(node.getRuleName());

        if (success) {
            gui.parseNodeStatus.setText("SUCCESS");
            gui.parseNodeStatus.setTextFill(Color.GREEN);
        } else {
            gui.parseNodeStatus.setText("FAILURE");
            gui.parseNodeStatus.setTextFill(Color.RED);
        }

        position = buffer.getPosition(node.getStart());
        gui.parseNodeStart.setText(String.format("line %d, column %d",
            position.getLine(), position.getColumn()));
        position = buffer.getPosition(node.getEnd());
        gui.parseNodeEnd.setText(String.format("line %d, column %d",
            position.getLine(), position.getColumn()));

        gui.parseNodeTime.setText(Utils.nanosToString(node.getNanos()));
    }

    @Override
    public void highlightText(final List<String> fragments,
        final Position position, final boolean success)
    {
        final TextFlow inputText = gui.inputText;
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

        final ScrollPane scroll = gui.inputTextScroll;
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
        private ParseNodeCell(final TraceTabGui ui)
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
