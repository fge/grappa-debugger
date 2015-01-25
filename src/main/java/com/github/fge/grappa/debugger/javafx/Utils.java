package com.github.fge.grappa.debugger.javafx;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;

public final class Utils
{
    @SuppressWarnings("ProhibitedExceptionThrown")
    private Utils()
    {
        throw new Error("nice try!");
    }

    @SuppressWarnings("AutoBoxing")
    public static String nanosToString(final long nanos)
    {
        long value = nanos;
        final long nrNanoseconds = value % 1000;
        value /= 1000;
        final long nrMicroseconds = value % 1000;
        value /= 1000;

        return String.format("%d ms, %03d.%03d µs", value, nrMicroseconds,
            nrNanoseconds);
    }

    public static <S, T> void bindColumn(final TableColumn<S, T> column,
        final String propertyName)
    {
        column.setCellValueFactory(new PropertyValueFactory<>(propertyName));
    }

    public static <S> void setDisplayNanos(final TableColumn<S, Long> column)
    {
        column.setCellFactory(param -> new TableCell<S, Long>()
        {
            @Override
            protected void updateItem(final Long item, final boolean empty)
            {
                super.updateItem(item, empty);
                //noinspection AutoUnboxing
                setText(empty ? null : nanosToString(item));
            }
        });
    }
}
