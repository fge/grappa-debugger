package com.github.fge.grappa.debugger.csvtrace.tabs.treedepth;

import com.github.fge.grappa.debugger.javafx.JavafxDisplay;
import com.github.fge.grappa.debugger.javafx.SmoothedAreaChart;
import com.google.common.annotations.VisibleForTesting;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.HBox;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collection;

public class TreeDepthTabDisplay
    extends JavafxDisplay<TreeDepthTabPresenter>
{
    public Separator refreshSeparator;
    public Label refreshText;
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
    protected HBox refreshBox;

    @FXML
    protected ProgressBar loadInProgress;

    @FXML
    protected Button refreshButton;

    @FXML
    protected SmoothedAreaChart<Number, Number> chart;

    @FXML
    protected NumberAxis xAxis;

    @FXML
    protected NumberAxis yAxis;

    protected final XYChart.Series<Number, Number> series
        = new XYChart.Series<>();

    protected Collection<Node> refreshNodes;

    @Override
    public void init()
    {
        hbox.minWidthProperty().bind(toolbar.widthProperty());

        refreshNodes = Arrays.asList(
            refreshSeparator, refreshText, refreshButton, loadInProgress
        );

        linesDisplayed.getItems().addAll(10, 25, 50);
        linesDisplayed.valueProperty().setValue(25);
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
                changeVisibleLinesEvent(newValue);
            }
        });
        
        chart.getData().add(series);
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
        presenter.handleChartRefresh();
    }

    @FXML
    void changeStartLineEvent(final ActionEvent event)
    {
        doChangeStartLineEvent(requiredLine.getText());
    }

    @VisibleForTesting
    void changeVisibleLinesEvent(final int visibleLines)
    {
        presenter.handleChangeVisibleLines(visibleLines);
    }

    @VisibleForTesting
    void doChangeStartLineEvent(@Nullable final String input)
    {
        if (input == null)
            return;
        try {
            final int startLine = Integer.parseInt(input);
            if (startLine >= 1)
                presenter.handleChangeStartLine(startLine);
        } catch (NumberFormatException ignored) {
        }
    }
}
