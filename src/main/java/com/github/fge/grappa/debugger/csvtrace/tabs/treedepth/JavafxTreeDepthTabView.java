package com.github.fge.grappa.debugger.csvtrace.tabs.treedepth;

import com.github.fge.grappa.debugger.javafx.JavafxView;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;

import java.io.IOException;
import java.util.List;

public final class JavafxTreeDepthTabView
    extends JavafxView<TreeDepthTabPresenter, TreeDepthTabDisplay>
    implements TreeDepthTabView
{
    public JavafxTreeDepthTabView()
        throws IOException
    {
        super("/tabs/treeDepthTab.fxml");
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

    @Override
    public void disableToolbar()
    {
        display.requiredLine.setDisable(true);
        display.linesDisplayed.setDisable(true);
        display.prevLines.setDisable(true);
        display.nextLines.setDisable(true);
    }

    @Override
    public void displayDepths(final int startLine, final int wantedLines,
        final List<Integer> depths)
    {
        final ObservableList<XYChart.Data<Number, Number>> list
            = display.series.getData();

        list.clear();

        display.xAxis.setLowerBound(startLine);
        final int endLine = startLine + wantedLines - 1;
        display.xAxis.setUpperBound(endLine);

        int lineNr = startLine;
        XYChart.Data<Number, Number> data;

        int maxDepth = 5;

        for (final Integer depth: depths) {
            if (depth > maxDepth)
                maxDepth = depth;
            data = new XYChart.Data<>(lineNr, depth);
            list.add(data);
            lineNr++;
        }

        display.yAxis.setUpperBound(maxDepth);
        final int tickUnit = maxDepth / 15;
        display.yAxis.setTickUnit(Math.max(tickUnit, 1));

        display.linesDisplayed.setDisable(false);

        display.currentLines.setText(String.format("Lines %d-%d", startLine,
            endLine));

        display.requiredLine.setText(String.valueOf(startLine));
        display.requiredLine.setDisable(false);
    }

    @Override
    public void setMaxLines(final int nrLines)
    {
        display.totalLines.setText(String.format("/ %d", nrLines));
    }

    @Override
    public void wakeUp()
    {
        display.requiredLine.setDisable(false);
        display.linesDisplayed.setDisable(false);
        display.prevLines.setDisable(false);
        display.nextLines.setDisable(false);
        display.pane.getChildren().remove(display.waitLabel);
        display.chart.setVisible(true);
    }
}
