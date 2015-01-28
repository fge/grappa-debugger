package com.github.fge.grappa.debugger.csvtrace;

import com.github.fge.grappa.internal.NonFinalForTesting;
import com.google.common.annotations.VisibleForTesting;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@ParametersAreNonnullByDefault
@NonFinalForTesting
public class CsvTraceDisplay
{
    private CsvTracePresenter presenter;

    @FXML
    Button treeExpand;

    void setPresenter(final CsvTracePresenter presenter)
    {
        this.presenter = Objects.requireNonNull(presenter);
        init();
    }

    @VisibleForTesting
    void init()
    {
    }

    @FXML
    void expandParseTreeEvent(final Event event)
    {
        presenter.handleExpandParseTree();
    }
}
