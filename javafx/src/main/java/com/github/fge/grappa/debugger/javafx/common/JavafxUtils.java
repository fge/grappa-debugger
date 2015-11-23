package com.github.fge.grappa.debugger.javafx.common;

import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TableColumn;
import javafx.scene.paint.Color;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public final class JavafxUtils
{
    public static final Color FILL_COLOR_1
        = Color.rgb(78, 164, 78);
    public static final Color FILL_COLOR_2
        = Color.rgb(234, 160, 41);
    public static final Color FILL_COLOR_3
        = Color.rgb(224, 97, 49);
    public static final Color FILL_COLOR_4
        = Color.rgb(72, 164, 192);

    public static final char MATCH_BEFORE = '\u21fe'; // right arrow
    public static final char MATCH_AFTER = '\u21fd';  // left arrow
    public static final String MATCH_EMPTY = "\u2205";     // empty set
    public static final String MATCH_FAILURE = "\u2612";   // ballot box

    public static final String CSS_STYLE_BEFOREMATCH = "beforeMatch";
    public static final String CSS_STYLE_MATCHFAILURE = "matchFailure";
    public static final String CSS_STYLE_MATCHSUCCESS = "matchSuccess";
    public static final String CSS_STYLE_AFTERMATCH = "afterMatch";

    public static final List<String> STYLE_BEFOREMATCH
        = Collections.singletonList(CSS_STYLE_BEFOREMATCH);
    public static final List<String> STYLE_MATCHSUCCESS
        = Collections.singletonList(CSS_STYLE_MATCHSUCCESS);
    public static final List<String> STYLE_MATCHFAILURE
        = Collections.singletonList(CSS_STYLE_MATCHFAILURE);
    public static final List<String> STYLE_AFTERMATCH
        = Collections.singletonList(CSS_STYLE_AFTERMATCH);

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
