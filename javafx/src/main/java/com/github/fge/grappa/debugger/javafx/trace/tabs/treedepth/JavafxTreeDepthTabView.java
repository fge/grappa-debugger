package com.github.fge.grappa.debugger.javafx.trace.tabs.treedepth;

import com.github.fge.grappa.debugger.ParseInfo;
import com.github.fge.grappa.debugger.javafx.common.JavafxView;
import com.github.fge.grappa.debugger.trace.tabs.treedepth.TreeDepthInfo;
import com.github.fge.grappa.debugger.trace.tabs.treedepth.TreeDepthTabPresenter;
import com.github.fge.grappa.debugger.trace.tabs.treedepth.TreeDepthTabView;
import com.google.common.annotations.VisibleForTesting;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Stream;

public final class JavafxTreeDepthTabView
    extends JavafxView<TreeDepthTabPresenter, TreeDepthTabDisplay>
    implements TreeDepthTabView
{
    public JavafxTreeDepthTabView()
        throws IOException
    {
        super("/javafx/tabs/treeDepth.fxml");
    }

    @Override
    public void disableTreeDepthToolbar()
    {
        Stream.of(display.requiredLine, display.linesDisplayed,
            display.prevLines, display.nextLines)
            .forEach(node -> node.setDisable(true));
    }

    @SuppressWarnings("AutoBoxing")
    @Override
    public void displayTreeDepthInfo(final TreeDepthInfo treeDepthInfo)
    {
        final int startLine = treeDepthInfo.getStartLine();
        final int endLine = treeDepthInfo.getEndLine();

        display.xAxis.setLowerBound(startLine);
        display.xAxis.setUpperBound(endLine);

        displayChart(treeDepthInfo.getDepths());

        String text;

        text = String.valueOf(startLine);

        display.requiredLine.setDisable(false);
        display.requiredLine.setText(text);

        text = String.format("%d-%d", startLine, endLine);
        display.currentLines.setText(text);

        display.prevLines.setDisable(!treeDepthInfo.hasPreviousLines());
        display.nextLines.setDisable(!treeDepthInfo.hasNextLines());

        display.linesDisplayed.setDisable(false);
    }

    @SuppressWarnings("AutoBoxing")
    @Override
    public void displayInfo(final ParseInfo info)
    {
        display.totalLines.setText(String.format("/ %d", info.getNrLines()));
    }

    @SuppressWarnings({ "AutoUnboxing", "AutoBoxing" })
    @VisibleForTesting
    void displayChart(final Map<Integer, Integer> depthMap)
    {
        final ObservableList<XYChart.Data<Number, Number>> list
            = display.series.getData();

        XYChart.Data<Number, Number> data;

        int maxDepth = 0;
        int depth;

        list.clear();

        for (final Map.Entry<Integer, Integer> entry: depthMap.entrySet()) {
            depth = entry.getValue();
            if (depth > maxDepth)
                maxDepth = depth;
            data = new XYChart.Data<>(entry.getKey(), depth);
            list.add(data);
        }

        display.yAxis.setUpperBound(maxDepth);
        display.yAxis.setTickUnit(Math.max(1, maxDepth / 15));
    }
}
