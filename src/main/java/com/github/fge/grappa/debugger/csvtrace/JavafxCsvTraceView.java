package com.github.fge.grappa.debugger.csvtrace;

import com.github.fge.grappa.debugger.common.GuiTaskRunner;
import com.github.fge.grappa.debugger.csvtrace.tabs.matches
    .JavafxMatchesTabView;
import com.github.fge.grappa.debugger.csvtrace.tabs.matches.MatchesTabPresenter;
import com.github.fge.grappa.debugger.csvtrace.tabs.rules.JavafxRulesTabView;
import com.github.fge.grappa.debugger.csvtrace.tabs.rules.RulesTabPresenter;
import com.github.fge.grappa.debugger.csvtrace.tabs.tree.JavafxTreeTabView;
import com.github.fge.grappa.debugger.csvtrace.tabs.tree.TreeTabPresenter;
import com.github.fge.grappa.debugger.javafx.JavafxView;
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
    public void loadTreeTab(final TreeTabPresenter tabPresenter)
    {
        Objects.requireNonNull(tabPresenter);

        final JavafxTreeTabView tabView;
        try {
            tabView = new JavafxTreeTabView(taskRunner);
        } catch (IOException e) {
            parentView.showError("Load error", "Unable to load parse tree", e);
            return;
        }
        tabView.getDisplay().setPresenter(tabPresenter);
        tabPresenter.setView(tabView);
        display.treeTab.setContent(tabView.getNode());
    }

    @Override
    public void loadRulesTab(final RulesTabPresenter tabPresenter)
    {
        Objects.requireNonNull(tabPresenter);

        final JavafxRulesTabView tabView;
        try {
            tabView = new JavafxRulesTabView();
        } catch (IOException e) {
            parentView.showError("Load error", "Unable to load statistics", e);
            return;
        }
        tabView.getDisplay().setPresenter(tabPresenter);
        tabPresenter.setView(tabView);
        display.rulesTab.setContent(tabView.getNode());
    }

    @Override
    public void loadMatchesTab(final MatchesTabPresenter tabPresenter)
    {
        Objects.requireNonNull(tabPresenter);

        final JavafxMatchesTabView tabView;
        try {
            tabView = new JavafxMatchesTabView();
        } catch (IOException e) {
            parentView.showError("Load error", "Unable to load statistics", e);
            return;
        }
        tabView.getDisplay().setPresenter(tabPresenter);
        tabPresenter.setView(tabView);
        display.matchesTab.setContent(tabView.getNode());
    }
}
