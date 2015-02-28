package com.github.fge.grappa.debugger.csvtrace;

import com.github.fge.grappa.debugger.GrappaDebuggerException;
import com.github.fge.grappa.debugger.common.BasePresenter;
import com.github.fge.grappa.debugger.common.GuiTaskRunner;
import com.github.fge.grappa.debugger.common.OnBackgroundThread;
import com.github.fge.grappa.debugger.common.OnUiThread;
import com.github.fge.grappa.debugger.common.TabPresenter;
import com.github.fge.grappa.debugger.csvtrace.tabs.matches.MatchesTabPresenter;
import com.github.fge.grappa.debugger.csvtrace.tabs.rules.RulesTabPresenter;
import com.github.fge.grappa.debugger.csvtrace.tabs.tree.TreeTabPresenter;
import com.github.fge.grappa.debugger.csvtrace.tabs.treedepth
    .TreeDepthTabPresenter;
import com.github.fge.grappa.debugger.mainwindow.MainWindowView;
import com.github.fge.grappa.internal.NonFinalForTesting;
import com.github.fge.lambdas.consumers.ThrowingConsumer;
import com.google.common.annotations.VisibleForTesting;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

@NonFinalForTesting
@ParametersAreNonnullByDefault
public class CsvTracePresenter
    extends BasePresenter<CsvTraceView>
{
    private final MainWindowView mainView;
    private final GuiTaskRunner taskRunner;
    private final CsvTraceModel model;

    @VisibleForTesting
    protected final Collection<TabPresenter<?>> tabs = new ArrayList<>();

    public CsvTracePresenter(final MainWindowView mainView,
        final GuiTaskRunner taskRunner, final CsvTraceModel model)
    {
        this.mainView = Objects.requireNonNull(mainView);
        this.taskRunner = taskRunner;
        this.model = Objects.requireNonNull(model);
    }

    @OnUiThread
    @Override
    public void load()
    {
        loadTreeTab();
        loadRulesTab();
        loadMatchesTab();
        loadTreeDepthTab();
    }

    @OnUiThread
    @VisibleForTesting
    void loadTreeTab()
    {
        final TreeTabPresenter tabPresenter = createTreeTabPresenter();
        view.loadTreeTab(tabPresenter);
        tabPresenter.load();
        tabs.add(tabPresenter);
    }

    @OnUiThread
    @VisibleForTesting
    TreeTabPresenter createTreeTabPresenter()
    {
        return new TreeTabPresenter(taskRunner, mainView, model);
    }

    @OnUiThread
    @VisibleForTesting
    void loadRulesTab()
    {
        final RulesTabPresenter tabPresenter = createRulesTabPresenter();
        view.loadRulesTab(tabPresenter);
        tabPresenter.load();
        tabs.add(tabPresenter);
    }

    @OnUiThread
    @VisibleForTesting
    RulesTabPresenter createRulesTabPresenter()
    {
        return new RulesTabPresenter(taskRunner, mainView, model);
    }

    @OnUiThread
    @VisibleForTesting
    void loadMatchesTab()
    {
        final MatchesTabPresenter tabPresenter = createMatchesTabPresenter();
        view.loadMatchesTab(tabPresenter);
        tabPresenter.load();
        tabs.add(tabPresenter);
    }

    @OnUiThread
    @VisibleForTesting
    MatchesTabPresenter createMatchesTabPresenter()
    {
        return new MatchesTabPresenter(taskRunner, model, mainView);
    }

    @OnUiThread
    @VisibleForTesting
    void loadTreeDepthTab()
    {
        final TreeDepthTabPresenter tabPresenter
            = createTreeDepthTabPresenter();
        view.loadTreeDepthTab(tabPresenter);
        tabPresenter.load();
        tabs.add(tabPresenter);
    }

    @OnUiThread
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

    @OnUiThread
    public void handleTabsRefreshEvent()
    {
        taskRunner.run(
            view::disableTabsRefresh,
            this::doRefreshTabs,
            this::postTabsRefresh
        );
    }

    @OnUiThread
    @VisibleForTesting
    void postTabsRefresh()
    {
        final Runnable runnable = model.isLoadComplete()
            ? view::showLoadComplete
            : view::enableTabsRefresh;

        runnable.run();
    }

    @OnBackgroundThread
    @VisibleForTesting
    void doRefreshTabs()
    {
        final ThrowingConsumer<CountDownLatch> await = CountDownLatch::await;

        tabs.stream().map(TabPresenter::refresh).forEach(await.orDoNothing());
    }
}
