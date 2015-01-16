package com.github.parboiled1.grappa.debugger.tracetab;

import com.github.parboiled1.grappa.trace.TraceEvent;
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
}
