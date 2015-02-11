package com.github.fge.grappa.debugger.csvtrace;

import com.github.fge.grappa.debugger.GrappaDebuggerException;
import com.github.fge.grappa.debugger.common.GuiTaskRunner;
import com.github.fge.grappa.debugger.csvtrace.tabs.matches.MatchesTabPresenter;
import com.github.fge.grappa.debugger.csvtrace.tabs.rules.RulesTabPresenter;
import com.github.fge.grappa.debugger.csvtrace.tabs.tree.TreeTabPresenter;
import com.github.fge.grappa.debugger.csvtrace.tabs.treedepth
    .TreeDepthTabPresenter;
import com.github.fge.grappa.debugger.javafx.BasePresenter;
import com.github.fge.grappa.debugger.javafx.TabPresenter;
import com.github.fge.grappa.debugger.mainwindow.MainWindowView;
import com.github.fge.grappa.internal.NonFinalForTesting;
import com.google.common.annotations.VisibleForTesting;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

@NonFinalForTesting
@ParametersAreNonnullByDefault
public class CsvTracePresenter
    extends BasePresenter<CsvTraceView>
{
    private final MainWindowView mainView;
    private final GuiTaskRunner taskRunner;
    private final CsvTraceModel model;

    @VisibleForTesting
    protected final Collection<TabPresenter<?>> tabs
        = new ArrayList<>();


    public CsvTracePresenter(final MainWindowView mainView,
        final GuiTaskRunner taskRunner, final CsvTraceModel model)
    {
        this.mainView = Objects.requireNonNull(mainView);
        this.taskRunner = taskRunner;
        this.model = Objects.requireNonNull(model);
    }

    @Override
    public void load()
    {
        loadTreeTab();
        loadRulesTab();
        loadMatchesTab();
        loadTreeDepthTab();
    }

    @VisibleForTesting
    void loadTreeTab()
    {
        final TreeTabPresenter tabPresenter = createTreeTabPresenter();
        view.loadTreeTab(tabPresenter);
        tabPresenter.load();
        tabs.add(tabPresenter);
    }

    @VisibleForTesting
    TreeTabPresenter createTreeTabPresenter()
    {
        return new TreeTabPresenter(taskRunner, mainView, model);
    }

    @VisibleForTesting
    void loadRulesTab()
    {
        final RulesTabPresenter tabPresenter = createRulesTabPresenter();
        view.loadRulesTab(tabPresenter);
        tabPresenter.load();
        tabs.add(tabPresenter);
    }

    @VisibleForTesting
    RulesTabPresenter createRulesTabPresenter()
    {
        return new RulesTabPresenter(taskRunner, mainView, model);
    }

    @VisibleForTesting
    void loadMatchesTab()
    {
        final MatchesTabPresenter tabPresenter = createMatchesTabPresenter();
        view.loadMatchesTab(tabPresenter);
        tabPresenter.load();
        tabs.add(tabPresenter);
    }

    @VisibleForTesting
    MatchesTabPresenter createMatchesTabPresenter()
    {
        return new MatchesTabPresenter(taskRunner, model, mainView);
    }

    // UNUSED...
    @VisibleForTesting
    void loadTreeDepthTab()
    {
        final TreeDepthTabPresenter tabPresenter
            = createTreeDepthTabPresenter();
        view.loadTreeDepthTab(tabPresenter);
        tabPresenter.load();
        tabs.add(tabPresenter);
    }

    @VisibleForTesting
    TreeDepthTabPresenter createTreeDepthTabPresenter()
    {
        return new TreeDepthTabPresenter(taskRunner, mainView, model);
    }

    public void dispose()
    {
        try {
            model.dispose();
        } catch (GrappaDebuggerException e) {
            mainView.showError("Trace file error", "Problem closing trace file",
                e);
        }
    }

    public void handleTabsRefreshEvent()
    {
        tabs.forEach(TabPresenter::refresh);
        if (model.isLoadComplete())
            loadComplete();
    }

    public void loadComplete()
    {
        view.showLoadComplete();
    }
}
