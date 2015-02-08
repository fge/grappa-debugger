package com.github.fge.grappa.debugger.csvtrace;

import com.github.fge.grappa.debugger.javafx.JavafxDisplay;
import com.github.fge.grappa.internal.NonFinalForTesting;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@NonFinalForTesting
public class CsvTraceDisplay
    extends JavafxDisplay<CsvTracePresenter>
{
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
}
