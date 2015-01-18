package com.github.parboiled1.grappa.debugger.tracetab;

import com.github.parboiled1.grappa.debugger.tracetab.statistics.InputTextInfo;
import com.github.parboiled1.grappa.debugger.tracetab.statistics.ParseNode;
import com.github.parboiled1.grappa.debugger.tracetab.statistics.RuleStatistics;
import com.github.parboiled1.grappa.trace.TraceEvent;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.Tab;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.List;
import java.util.function.LongFunction;

public final class DefaultTraceTabView
    implements TraceTabView
{
    private static final LongFunction<String> NANOS_TO_STRING = nanos -> {
        long value = nanos;
        final long nrNanoseconds = value % 1000;
        value /= 1000;
        final long nrMicroseconds = value % 1000;
        value /= 1000;

        return String.format("%d ms, %03d.%03d µs", value, nrMicroseconds,
            nrNanoseconds);
    };

    private final TraceTabUi ui;

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
        bindColumn(ui.statsTotalTime, "totalTime");
        setDisplayNanos(ui.statsTotalTime);
        ui.statsAvgTime.setCellValueFactory(
            param -> new SimpleObjectProperty<Long>()
            {
                @SuppressWarnings("AutoBoxing")
                @Override
                public Long get()
                {
                    final RuleStatistics stats = param.getValue();
                    return stats.getTotalTime() / stats.getNrInvocations();
                }
            }
        );
        setDisplayNanos(ui.statsAvgTime);
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
        ui.totalParseTime.setText(NANOS_TO_STRING.apply(nanos));
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
    public void setInputText(final InputTextInfo textInfo)
    {
        ui.inputNrChars.setText(String.valueOf(textInfo.getNrChars()));
        ui.inputNrCodePoints
            .setText(String.valueOf(textInfo.getNrCodePoints()));
        ui.inputNrLines.setText(String.valueOf(textInfo.getNrLines()));
        ui.inputText.getChildren().add(new Text(textInfo.getContents()));
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
                setText(empty ? null : NANOS_TO_STRING.apply(item));
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
