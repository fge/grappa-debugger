package com.github.fge.grappa.debugger.csvtrace.tabs.linechart;

import com.github.fge.grappa.debugger.common.JavafxDisplay;
import javafx.fxml.FXML;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedAreaChart;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

public class LineChartTabDisplay
    extends JavafxDisplay<LineChartTabPresenter>
{
    @FXML
    protected BorderPane pane;

    @FXML
    protected ToolBar toolbar;

    @FXML
    protected HBox hbox;

    protected final NumberAxis xAxis = new NumberAxis();
    protected final NumberAxis yAxis = new NumberAxis();
    protected final StackedAreaChart<Number, Number> chart
        = new StackedAreaChart<>(xAxis, yAxis);

    @Override
    public void init()
    {
        hbox.minWidthProperty().bind(toolbar.widthProperty());

        chart.setAnimated(false);
        chart.setTitle("Matcher status by line");
        pane.setCenter(chart);
    }
}
