package com.github.fge.grappa.debugger.csvtrace;

import com.github.fge.grappa.debugger.common.GuiTaskRunner;
import com.github.fge.grappa.debugger.common.JavafxView;
import com.github.fge.grappa.debugger.csvtrace.tabs.stats.JavafxStatsTabView;
import com.github.fge.grappa.debugger.csvtrace.tabs.stats.StatsTabPresenter;
import com.github.fge.grappa.debugger.csvtrace.tabs.tree.JavafxTreeTabView;
import com.github.fge.grappa.debugger.csvtrace.tabs.tree.TreeTabPresenter;
import com.github.fge.grappa.debugger.mainwindow.MainWindowView;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.util.Objects;

@ParametersAreNonnullByDefault
public final class JavafxCsvTraceView
    extends JavafxView<CsvTracePresenter, CsvTraceDisplay>
    implements CsvTraceView
{
    private final GuiTaskRunner taskRunner;
    private final MainWindowView parentView;

    public JavafxCsvTraceView(final GuiTaskRunner taskRunner,
        final MainWindowView parentView)
        throws IOException
    {
        super("/csvTrace.fxml");
        this.taskRunner = Objects.requireNonNull(taskRunner);
        this.parentView = Objects.requireNonNull(parentView);
    }

    @Override
    public void loadTreeTab(final TreeTabPresenter presenter)
    {
        Objects.requireNonNull(presenter);

        final JavafxTreeTabView tabView;
        try {
            tabView = new JavafxTreeTabView(taskRunner);
        } catch (IOException e) {
            parentView.showError("Load error", "Unable to load parse tree", e);
            return;
        }
        tabView.getDisplay().setPresenter(presenter);
        presenter.setView(tabView);
        display.treeTab.setContent(tabView.getNode());
    }

    @Override
    public void loadStatsTab(final StatsTabPresenter presenter)
    {
        Objects.requireNonNull(presenter);

        final JavafxStatsTabView tabView;
        try {
            tabView = new JavafxStatsTabView();
        } catch (IOException e) {
            parentView.showError("Load error", "Unable to load statistics", e);
            return;
        }
        tabView.getDisplay().setPresenter(presenter);
        presenter.setView(tabView);
        display.statsTab.setContent(tabView.getNode());
    }
}
