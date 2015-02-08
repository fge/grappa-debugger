package com.github.fge.grappa.debugger.csvtrace.tabs.treedepth;

import com.github.fge.grappa.debugger.javafx.JavafxDisplay;
import com.github.fge.grappa.debugger.javafx.SmoothedAreaChart;
import com.google.common.annotations.VisibleForTesting;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.HBox;

public class TreeDepthTabDisplay
    extends JavafxDisplay<TreeDepthTabPresenter>
{
    @FXML
    protected ToolBar toolbar;

    @FXML
    protected HBox hbox;

    @FXML
    protected ComboBox<Integer> linesDisplayed;

    @FXML
    protected Button prevLines;

    @FXML
    protected Label currentLines;

    @FXML
    protected Button nextLines;

    @FXML
    protected HBox refreshBox;

    @FXML
    protected Button tabRefresh;

    @FXML
    protected ProgressBar progressBar;

    @FXML
    protected SmoothedAreaChart<Number, Number> chart;

    @FXML
    protected NumberAxis xAxis;

    @FXML
    protected NumberAxis yAxis;

    protected final XYChart.Series<Number, Number> series
        = new XYChart.Series<>();

    @Override
    public void init()
    {
        hbox.minWidthProperty().bind(toolbar.widthProperty());

        linesDisplayed.getItems().addAll(10, 25, 50);
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
                displayLinesEvent(newValue);
            }
        });
        
        chart.getData().add(series);
    }

    @VisibleForTesting
    void displayLinesEvent(final int nrLines)
    {
        presenter.handleDisplayedLines(nrLines);
    }

    @FXML
    void previousLinesEvent(final Event event)
    {
        presenter.handlePreviousLines();
    }

    @FXML
    void nextLinesEvent(final Event event)
    {
        presenter.handleNextLines();
    }

    @FXML
    void chartRefreshEvent(final Event event)
    {
        presenter.handleChartRefreshEvent();
    }
}
