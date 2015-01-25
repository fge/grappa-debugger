package com.github.fge.grappa.debugger.javafx;

import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;

import java.util.function.Function;

public final class JavafxUtils
{
    @SuppressWarnings("ProhibitedExceptionThrown")
    private JavafxUtils()
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

    public static <S, T> void setColumnValue(final TableColumn<S, T> column,
        final Function<? super S, ? extends T> f)
    {
        column.setCellValueFactory(
            param -> new SimpleObjectProperty<T>()
            {
                @Override
                public T get()
                {
                    return f.apply(param.getValue());
                }
            }
        );
    }
}
