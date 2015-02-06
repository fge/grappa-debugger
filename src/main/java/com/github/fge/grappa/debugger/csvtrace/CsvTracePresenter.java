package com.github.fge.grappa.debugger.csvtrace;

import com.github.fge.grappa.debugger.common.GuiTaskRunner;
import com.github.fge.grappa.debugger.common.TracePresenter;
import com.github.fge.grappa.debugger.csvtrace.tabs.stats.StatsTabPresenter;
import com.github.fge.grappa.debugger.csvtrace.tabs.tree.TreeTabPresenter;
import com.github.fge.grappa.debugger.mainwindow.MainWindowView;
import com.github.fge.grappa.internal.NonFinalForTesting;
import com.google.common.annotations.VisibleForTesting;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@NonFinalForTesting
@ParametersAreNonnullByDefault
public class CsvTracePresenter
    extends TracePresenter<CsvTraceView>
{
    private final MainWindowView mainView;
    private final GuiTaskRunner taskRunner;
    private final CsvTraceModel model;

    public CsvTracePresenter(final MainWindowView mainView,
        final GuiTaskRunner taskRunner, final CsvTraceModel model)
    {
        this.mainView = Objects.requireNonNull(mainView);
        this.taskRunner = taskRunner;
        this.model = Objects.requireNonNull(model);
    }

    @Override
    public void loadTrace()
    {
        loadTreeTab();
        loadStatsTab();
        loadMatchesTab();
    }

    @VisibleForTesting
    void loadTreeTab()
    {
        final TreeTabPresenter treeTab = createTreeTabPresenter();
        view.loadTreeTab(treeTab);
        treeTab.load();
    }

    @VisibleForTesting
    TreeTabPresenter createTreeTabPresenter()
    {
        return new TreeTabPresenter(taskRunner, mainView, model);
    }

    @VisibleForTesting
    void loadStatsTab()
    {
        final StatsTabPresenter statsTab = createStatsTabPresenter();
        view.loadStatsTab(statsTab);
        statsTab.load();
    }

    @VisibleForTesting
    StatsTabPresenter createStatsTabPresenter()
    {
        return new StatsTabPresenter(taskRunner, mainView, model);
    }

    public void loadMatchesTab()
    {
        // TODO

    }

    @Override
    public void dispose()
    {
        try {
            model.dispose();
        } catch (Exception e) {
            mainView.showError("Trace file error", "Problem closing trace file",
                e);
        }
    }
}
