package com.github.fge.grappa.debugger.csvtrace.tabs.linechart;

import com.github.fge.grappa.debugger.common.JavafxDisplay;
import com.google.common.annotations.VisibleForTesting;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedAreaChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ProgressBar;
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

    @FXML
    protected ComboBox<Integer> linesDisplayed;

    @FXML
    protected Button tabRefresh;

    @FXML
    protected ProgressBar progressBar;

    protected NumberAxis xAxis;
    protected NumberAxis yAxis;
    protected StackedAreaChart<Number, Number> chart;

    protected final XYChart.Series<Number, Number> waitingSeries
        = new XYChart.Series<>();
    protected final XYChart.Series<Number, Number> startedSeries
        = new XYChart.Series<>();
    protected final XYChart.Series<Number, Number> successSeries
        = new XYChart.Series<>();
    protected final XYChart.Series<Number, Number> failureSeries
        = new XYChart.Series<>();

    @Override
    public void init()
    {
        hbox.minWidthProperty().bind(toolbar.widthProperty());

        linesDisplayed.getItems().addAll(10, 25, 50, 100);
        linesDisplayed.valueProperty().addListener(new ChangeListener<Integer>()
        {
            @Override
            public void changed(
                final ObservableValue<? extends Integer> observable,
                final Integer oldValue, final Integer newValue)
            {
                if (oldValue != null && oldValue.equals(newValue))
                    return;
                if (newValue == null)
                    return;
                //noinspection AutoUnboxing
                changeLinesDisplayedEvent(newValue);
            }
        });

        xAxis = new NumberAxis();
        xAxis.setLabel("Line number");
        xAxis.setForceZeroInRange(false);
        xAxis.setTickUnit(1.0);
        xAxis.setTickMarkVisible(false);

        yAxis = new NumberAxis();
        yAxis.setTickMarkVisible(false);
        yAxis.setTickMarkVisible(false);


        chart = new StackedAreaChart<>(xAxis, yAxis);

        chart.setAnimated(false);
        chart.setTitle("Matcher status by line");

        waitingSeries.setName("Waiting for children");
        startedSeries.setName("Started this line");
        successSeries.setName("Succeeded this line");
        failureSeries.setName("Failed this line");

        final ObservableList<XYChart.Series<Number, Number>> data
            = chart.getData();

        data.add(waitingSeries);
        data.add(startedSeries);
        data.add(successSeries);
        data.add(failureSeries);

        pane.setCenter(chart);
    }

    @VisibleForTesting
    protected void changeLinesDisplayedEvent(final int nrLines)
    {
        presenter.handleChangeLinesDisplayed(nrLines);
    }
}
