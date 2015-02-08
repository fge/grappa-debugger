package com.github.fge.grappa.debugger.csvtrace.tabs.linechart;

import com.github.fge.grappa.debugger.common.JavafxView;
import com.github.fge.grappa.debugger.csvtrace.newmodel.LineMatcherStatus;
import javafx.scene.chart.XYChart;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

public final class JavafxLineChartTabView
    extends JavafxView<LineChartTabPresenter, LineChartTabDisplay>
    implements LineChartTabView
{
    public JavafxLineChartTabView()
        throws IOException
    {
        super("/tabs/lineChartTab.fxml");
    }

    @Override
    public void disableTabRefresh()
    {
        display.tabRefresh.setDisable(true);
        display.prevLines.setDisable(true);
        display.nextLines.setDisable(true);
        display.progressBar.setVisible(true);
    }

    @Override
    public void showLineMatcherStatus(final List<LineMatcherStatus> list,
        final int startLine, final int nrLines)
    {
        display.xAxis.setLowerBound(startLine);
        display.xAxis.setUpperBound(startLine + nrLines - 1);

        XYChart.Data<Number, Number> data;

        int lineNr = startLine;

        allSeries().forEach(series -> series.getData().clear());

        for (final LineMatcherStatus status: list) {
            data = new XYChart.Data<>(lineNr, status.getNrWaiting());
            display.waitingSeries.getData().add(data);
            data = new XYChart.Data<>(lineNr, status.getNrStarted());
            display.startedSeries.getData().add(data);
            data = new XYChart.Data<>(lineNr, status.getNrSuccess());
            display.successSeries.getData().add(data);
            data = new XYChart.Data<>(lineNr, status.getNrFailures());
            display.failureSeries.getData().add(data);
            lineNr++;
        }

        display.currentLines.setText(String.format("Lines %d-%d",
            startLine, startLine + nrLines - 1));

        display.progressBar.setVisible(false);
    }

    @Override
    public void showLoadComplete()
    {
        display.refreshBox.getChildren().remove(display.tabRefresh);
    }

    @Override
    public void showLoadIncomplete()
    {
        display.tabRefresh.setDisable(false);
    }

    @Override
    public void disablePrevious()
    {
        display.prevLines.setDisable(true);
    }

    @Override
    public void disableNext()
    {
        display.nextLines.setDisable(true);
    }

    @Override
    public void enablePrevious()
    {
        display.prevLines.setDisable(false);
    }

    @Override
    public void enableNext()
    {
        display.nextLines.setDisable(false);
    }

    private Stream<XYChart.Series<Number, Number>> allSeries()
    {
        return Stream.of(display.waitingSeries, display.startedSeries,
            display.successSeries, display.failureSeries);
    }
}
