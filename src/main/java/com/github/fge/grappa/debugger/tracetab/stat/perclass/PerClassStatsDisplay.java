package com.github.fge.grappa.debugger.tracetab.stat.perclass;

import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@ParametersAreNonnullByDefault
public class PerClassStatsDisplay
{
    private PerClassStatsPresenter presenter;

    @FXML
    PieChart ruleChart;

    public void setPresenter(final PerClassStatsPresenter presenter)
    {
        this.presenter = Objects.requireNonNull(presenter);
    }
}
