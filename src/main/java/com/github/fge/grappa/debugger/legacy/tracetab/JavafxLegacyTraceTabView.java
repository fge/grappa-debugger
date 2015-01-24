package com.github.fge.grappa.debugger.legacy.tracetab;

import com.github.fge.grappa.buffers.InputBuffer;
import com.github.fge.grappa.debugger.legacy.LegacyTraceEvent;
import com.github.fge.grappa.debugger.legacy.RuleStatistics;
import com.github.fge.grappa.debugger.statistics.ParseNode;
import com.github.fge.grappa.debugger.statistics.TracingCharEscaper;
import com.github.fge.grappa.debugger.statistics.Utils;
import com.github.fge.grappa.trace.TraceEventType;
import com.google.common.escape.CharEscaper;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.scene.chart.PieChart.Data;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import org.parboiled.support.Position;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public final class JavafxLegacyTraceTabView
    implements LegacyTraceTabView
{
    private static final ThreadFactory THREAD_FACTORY
        = new ThreadFactoryBuilder().setDaemon(true)
            .setNameFormat("tree-expand-%d").build();
    private static final CharEscaper ESCAPER = new TracingCharEscaper();

    private final ExecutorService executor
        = Executors.newSingleThreadExecutor(THREAD_FACTORY);

    private final LegacyTraceTabDisplay display;

    private InputBuffer buffer;
    private int treeDepth = 0;

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
                    return 100.0 * stats.getNrSuccesses() / stats
                        .getNrInvocations();
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

    @SuppressWarnings({ "TypeMayBeWeakened", "AutoBoxing" })
    @Override
    public void setStatistics(final Collection<RuleStatistics> values,
        final int nrEmptyMatches)
    {
        display.nrRules.setText(String.valueOf(values.size()));
        display.stats.getItems().setAll(values);

        int totalInvocations = 0;
        int totalSuccesses = 0;

        for (final RuleStatistics stats: values) {
            totalInvocations += stats.getNrInvocations();
            totalSuccesses += stats.getNrSuccesses();
        }

        final List<Data> list = new ArrayList<>(3);

        int nr;
        double percent;
        String fmt;

        /*
         * Failures
         */
        nr = totalInvocations - totalSuccesses;
        percent = 100.0 * nr / totalInvocations;
        fmt = String.format("Failures (%d - %.02f%%)", nr, percent);
        list.add(new Data(fmt, percent));

        /*
         * Empty
         */
        nr = nrEmptyMatches;
        percent = 100.0 * nr / totalInvocations;
        fmt = String.format("Empty matches (%d - %.02f%%)", nr, percent);
        list.add(new Data(fmt, percent));

        /*
         * Non empty
         */
        nr = totalSuccesses - nrEmptyMatches;
        percent = 100.0 * nr / totalInvocations;
        fmt = String.format("Non empty matches (%d; %.02f%%)", nr, percent);
        list.add(new Data(fmt, percent));

        display.matchChart.getData().setAll(list);

        fmt = String.format("Rule rundown (%d total)", totalInvocations);
        display.matchChart.setTitle(fmt);

        display.invPerLine.setText(String.format("%.02f",
            (double) totalInvocations / buffer.getLineCount()));
        display.invPerChar.setText(String.format("%.02f",
            (double) totalInvocations / buffer.length()));
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
    public void setInputBuffer(final InputBuffer buffer)
    {
        this.buffer = buffer;

        final int nrLines = buffer.getLineCount();
        final int nrChars = buffer.length();
        final String text = buffer.extract(0, nrChars);
        final int nrCodePoints = text.codePointCount(0, nrChars);

        display.inputText.getChildren().setAll(new Text(text));
        display.textInfo.setText(nrLines + " lines, " + nrChars
            + " characters, " + nrCodePoints + " code points");
    }

    @Override
    public void setParseTree(final ParseNode node)
    {
        display.parseTree.setRoot(buildTree(node));
        display.treeDepth.setText(String.valueOf(treeDepth));
    }

    @SuppressWarnings("AutoBoxing")
    @Override
    public void fillParseNodeDetails(final ParseNode node)
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
    public void expandParseTree()
    {
        final TreeItem<ParseNode> root = display.parseTree.getRoot();
        final ParseNode node = root.getValue();
        final Button button = display.treeExpand;

        button.setDisable(true);
        button.setText("Please wait...");

        executor.submit(() -> {
            final TreeItem<ParseNode> newRoot = buildTree(node, true);
            Platform.runLater(() -> {
                display.parseTree.setRoot(newRoot);
                button.setText("Expand tree");
                button.setDisable(false);
            });
        });
    }

    @Override
    public void highlightSuccess(final int start, final int end)
    {
        final int length = buffer.length();
        final List<Text> list = new ArrayList<>(3);

        String fragment;
        Text text;

        // Before match
        fragment = buffer.extract(0, start);
        if (!fragment.isEmpty()) {
            text = new Text(fragment);
            text.setFill(Color.GRAY);
            list.add(text);
        }

        // Match
        fragment = buffer.extract(start, end);
        text = new Text(fragment.isEmpty() ? "\u2205"
            : '\u21fe' + ESCAPER.escape(fragment) + '\u21fd');
        text.setFill(Color.GREEN);
        text.setUnderline(true);
        list.add(text);

        // After match
        fragment = buffer.extract(end, length);
        if (!fragment.isEmpty()) {
            text = new Text(fragment);
            list.add(text);
        }

        display.inputText.getChildren().setAll(list);
        setScroll(start);
    }

    @Override
    public void highlightFailure(final int end)
    {
        final int length = buffer.length();
        final List<Text> list = new ArrayList<>(3);

        String fragment;
        Text text;

        fragment = buffer.extract(0, end);
        if (!fragment.isEmpty()) {
            text = new Text(fragment);
            text.setFill(Color.GRAY);
            list.add(text);
        }

        text = new Text("\u2612");
        text.setFill(Color.RED);
        text.setUnderline(true);
        list.add(text);

        fragment = buffer.extract(end, length);
        if (!fragment.isEmpty())
            list.add(new Text(fragment));

        display.inputText.getChildren().setAll(list);
        setScroll(end);
    }

    private void setScroll(final int index)
    {
        final Position position = buffer.getPosition(index);
        double line = position.getLine();
        final double nrLines = buffer.getLineCount();
        if (line != nrLines)
            line--;
        display.inputTextScroll.setVvalue(line / nrLines);
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
        private ParseNodeCell(final LegacyTraceTabDisplay display)
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

    private TreeItem<ParseNode> buildTree(final ParseNode root)
    {
        return buildTree(root, false);
    }

    private TreeItem<ParseNode> buildTree(final ParseNode root,
        final boolean expanded)
    {
        final TreeItem<ParseNode> ret = new TreeItem<>(root);

        addChildren(ret, root, expanded);

        return ret;
    }

    private void addChildren(final TreeItem<ParseNode> item,
        final ParseNode parent, final boolean expanded)
    {
        final int depth = parent.getLevel();
        if (depth > treeDepth)
            treeDepth = depth;

        TreeItem<ParseNode> childItem;
        final List<TreeItem<ParseNode>> childrenItems
            = FXCollections.observableArrayList();

        for (final ParseNode node: parent.getChildren()) {
            childItem = new TreeItem<>(node);
            addChildren(childItem, node, expanded);
            childrenItems.add(childItem);
        }

        item.getChildren().setAll(childrenItems);
        item.setExpanded(expanded);
    }
}
