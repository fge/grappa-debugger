package com.github.fge.grappa.debugger.csvtrace.tabs.treedepth;

import com.github.fge.grappa.debugger.javafx.JavafxView;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

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
    public void disableToolbar()
    {
        Stream.of(display.requiredLine, display.linesDisplayed,
            display.prevLines, display.nextLines, display.refreshButton
        ).forEach(node -> node.setDisable(true));
        display.loadInProgress.setVisible(true);
    }

    void displayDepths(final int startLine, final int wantedLines,
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
    public void displayChart(final Map<Integer, Integer> same)
    {
        // TODO
    }

    @Override
    public void setTreeDepth(final int depth)
    {
        display.yAxis.setUpperBound(depth);
        final int tick = Math.min(depth / 15, 1);
        display.yAxis.setTickUnit(tick);
    }

    @Override
    public void updateStartLine(final int startLine)
    {
        display.requiredLine.setText(String.valueOf(startLine));
    }

    @Override
    public void updateToolbar(final boolean disablePrev,
        final boolean disableNext, final boolean disableRefresh)
    {
        display.linesDisplayed.setDisable(false);
        display.prevLines.setDisable(disablePrev);
        display.nextLines.setDisable(disableNext);
        if (disableRefresh)
            display.hbox.getChildren().remove(display.refreshBox);
        else {
            display.refreshButton.setDisable(false);
            display.loadInProgress.setVisible(false);
        }
    }
}
