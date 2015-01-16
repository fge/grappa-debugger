package com.github.parboiled1.grappa.debugger.tracetab;

import com.github.parboiled1.grappa.trace.TraceEvent;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public final class DefaultTraceTabView
    implements TraceTabView
{
    private final TraceTabUi ui;

    public DefaultTraceTabView(final TraceTabUi ui)
    {
        this.ui = ui;
        bindColumn(ui.eventTime, "nanoseconds");
        ui.eventTime.setCellFactory(param -> new TableCell<TraceEvent, Long>()
        {
            @Override
            protected void updateItem(final Long item, final boolean empty)
            {
                super.updateItem(item, empty);
                setText(empty ? null : nanosToText(item));
            }
        });
        bindColumn(ui.eventDepth, "level");
        bindColumn(ui.eventIndex, "index");
        bindColumn(ui.eventPath, "path");
        bindColumn(ui.eventRule, "matcher");
        bindColumn(ui.eventType, "type");
    }

    @Override
    public void setTraceEvents(final List<TraceEvent> events)
    {
        ui.events.getItems().setAll(events);
    }

    private static <T> void bindColumn(final TableColumn<TraceEvent, T> column,
        final String propertyName)
    {
        column.setCellValueFactory(new PropertyValueFactory<>(propertyName));
    }

    @SuppressWarnings("AutoBoxing")
    private static String nanosToText(final long nanos)
    {
        long value = nanos;
        final long nrNanoseconds = value % 1000;
        value /= 1000;
        final long nrMicroseconds = value % 1000;
        value /= 1000;

        return String.format("%d ms, %03d.%03d µs", value, nrMicroseconds,
            nrNanoseconds);
    }
}
