package com.github.fge.grappa.debugger.csvtrace;

import com.github.fge.grappa.debugger.common.GuiTaskRunner;
import com.github.fge.grappa.debugger.csvtrace.tabs.matches
    .JavafxMatchesTabView;
import com.github.fge.grappa.debugger.csvtrace.tabs.matches.MatchesTabPresenter;
import com.github.fge.grappa.debugger.csvtrace.tabs.rules.JavafxRulesTabView;
import com.github.fge.grappa.debugger.csvtrace.tabs.rules.RulesTabPresenter;
import com.github.fge.grappa.debugger.csvtrace.tabs.tree.JavafxTreeTabView;
import com.github.fge.grappa.debugger.csvtrace.tabs.tree.TreeTabPresenter;
import com.github.fge.grappa.debugger.csvtrace.tabs.treedepth
    .JavafxTreeDepthTabView;
import com.github.fge.grappa.debugger.csvtrace.tabs.treedepth
    .TreeDepthTabPresenter;
import com.github.fge.grappa.debugger.javafx.JavafxView;
import com.github.fge.grappa.debugger.mainwindow.MainWindowView;
import com.github.fge.grappa.internal.NonFinalForTesting;
import com.google.common.annotations.VisibleForTesting;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.util.Objects;

@ParametersAreNonnullByDefault
@NonFinalForTesting
public class JavafxCsvTraceView
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
    public void loadTreeTab(final TreeTabPresenter tabPresenter)
    {
        Objects.requireNonNull(tabPresenter);

        final JavafxTreeTabView tabView;
        try {
            tabView = getTreeTabView();
        } catch (IOException e) {
            parentView.showError("Load error", "Unable to load tree tab", e);
            return;
        }
        tabView.getDisplay().setPresenter(tabPresenter);
        tabPresenter.setView(tabView);
        display.treeTab.setContent(tabView.getNode());
    }

    @VisibleForTesting
    JavafxTreeTabView getTreeTabView()
        throws IOException
    {
        return new JavafxTreeTabView(taskRunner);
    }

    @Override
    public void loadRulesTab(final RulesTabPresenter tabPresenter)
    {
        Objects.requireNonNull(tabPresenter);

        final JavafxRulesTabView tabView;
        try {
            tabView = getRulesTabView();
        } catch (IOException e) {
            parentView.showError("Load error", "Unable to load rules tab", e);
            return;
        }
        tabView.getDisplay().setPresenter(tabPresenter);
        tabPresenter.setView(tabView);
        display.rulesTab.setContent(tabView.getNode());
    }

    @VisibleForTesting
    JavafxRulesTabView getRulesTabView()
        throws IOException
    {
        return new JavafxRulesTabView();
    }

    @Override
    public void loadMatchesTab(final MatchesTabPresenter tabPresenter)
    {
        Objects.requireNonNull(tabPresenter);

        final JavafxMatchesTabView tabView;
        try {
            tabView = getMatchesTabView();
        } catch (IOException e) {
            parentView.showError("Load error", "Unable to load matches tab", e);
            return;
        }
        tabView.getDisplay().setPresenter(tabPresenter);
        tabPresenter.setView(tabView);
        display.matchesTab.setContent(tabView.getNode());
    }

    @VisibleForTesting
    JavafxMatchesTabView getMatchesTabView()
        throws IOException
    {
        return new JavafxMatchesTabView();
    }

    @Override
    public void loadTreeDepthTab(final TreeDepthTabPresenter tabPresenter)
    {
        Objects.requireNonNull(tabPresenter);

        final JavafxTreeDepthTabView tabView;
        try {
            tabView = getTreeDepthTabView();
        } catch (IOException e) {
            parentView.showError("Load error", "Unable to load statistics", e);
            return;
        }
        tabView.getDisplay().setPresenter(tabPresenter);
        tabPresenter.setView(tabView);
        display.treeDepthTab.setContent(tabView.getNode());
    }

    @VisibleForTesting
    JavafxTreeDepthTabView getTreeDepthTabView()
        throws IOException
    {
        return new JavafxTreeDepthTabView();
    }

    @Override
    public void showLoadComplete()
    {
        display.pane.setTop(null);
    }

    @Override
    public void disableTabsRefresh()
    {
        display.refresh.setDisable(true);
    }

    @Override
    public void enableTabsRefresh()
    {
        display.refresh.setDisable(false);
    }
}
