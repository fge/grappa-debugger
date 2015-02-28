package com.github.fge.grappa.debugger.javafx.csvtrace;

import com.github.fge.grappa.debugger.csvtrace.CsvTracePresenter;
import com.github.fge.grappa.debugger.javafx.common.JavafxDisplay;
import com.github.fge.grappa.internal.NonFinalForTesting;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tab;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@NonFinalForTesting
public class CsvTraceDisplay
    extends JavafxDisplay<CsvTracePresenter>
{
    @FXML
    protected BorderPane pane;

    @FXML
    protected ToolBar toolbar;

    @FXML
    protected Button refresh;

    @FXML
    protected ProgressBar progressBar;

    @FXML
    protected Tab treeTab;

    @FXML
    protected Tab rulesTab;

    @FXML
    protected Tab matchesTab;

    @FXML
    protected Tab treeDepthTab;

    @Override
    public void init()
    {
    }

    public void tabsRefreshEvent(Event event)
    {
        presenter.handleTabsRefreshEvent();
    }
}
