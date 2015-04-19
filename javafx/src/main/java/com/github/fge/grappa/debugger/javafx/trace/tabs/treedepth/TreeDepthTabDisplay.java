package com.github.fge.grappa.debugger.javafx.trace.tabs.treedepth;

import com.github.fge.grappa.debugger.javafx.common.JavafxDisplay;
import com.github.fge.grappa.debugger.trace.tabs.treedepth.TreeDepthTabPresenter;
import com.google.common.annotations.VisibleForTesting;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.HBox;
import javafx.util.StringConverter;

public class TreeDepthTabDisplay
    extends JavafxDisplay<TreeDepthTabPresenter>
{
    @FXML
    protected ToolBar toolbar;

    @FXML
    protected TextField requiredLine;

    @FXML
    protected Label totalLines;

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
        linesDisplayed.valueProperty().setValue(25);
        linesDisplayed.valueProperty().addListener(
            (observable, oldValue, newValue) -> {
                if (oldValue != null && oldValue.equals(newValue))
                    return;
                if (newValue == null)
                    return;
                //noinspection AutoUnboxing
                changeDisplayedLinesEvent(newValue);
            });
        
        chart.getData().add(series);

        xAxis.setTickLabelFormatter(new StringConverter<Number>()
        {
            @Override
            public String toString(final Number object)
            {
                return String.valueOf(object.intValue());
            }

            @Override
            public Number fromString(final String string)
            {
                //noinspection AutoBoxing
                return Integer.parseInt(string);
            }
        });
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
    void changeStartLineEvent(final ActionEvent event)
    {
        presenter.handleChangeStartLine(requiredLine.getText());
    }

    @VisibleForTesting
    void changeDisplayedLinesEvent(final int displayedLines)
    {
        presenter.handleChangedDisplayedLines(displayedLines);
    }
}
