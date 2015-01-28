package com.github.fge.grappa.debugger.csvtrace;

import com.github.fge.grappa.debugger.mainwindow.MainWindowView;
import com.github.fge.grappa.internal.NonFinalForTesting;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@NonFinalForTesting
@ParametersAreNonnullByDefault
public class CsvTracePresenter
{
    private final MainWindowView mainView;
    private final CsvTraceModel model;

    private CsvTraceView view;

    public CsvTracePresenter(final MainWindowView mainView,
        final CsvTraceModel model)
    {
        this.mainView = Objects.requireNonNull(mainView);
        this.model = Objects.requireNonNull(model);
    }

    public void setView(final CsvTraceView view)
    {
        this.view = Objects.requireNonNull(view);
    }

    void handleExpandParseTree()
    {
        // TODO

    }
}
