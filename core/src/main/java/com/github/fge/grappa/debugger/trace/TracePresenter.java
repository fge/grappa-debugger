package com.github.fge.grappa.debugger.trace;

import com.github.fge.grappa.debugger.ParseInfo;
import com.github.fge.grappa.debugger.TraceDb;
import com.github.fge.grappa.debugger.TraceDbLoadStatus;
import com.github.fge.grappa.debugger.common.BasePresenter;
import com.github.fge.grappa.debugger.common.GuiTaskRunner;
import com.github.fge.grappa.debugger.common.OnBackgroundThread;
import com.github.fge.grappa.debugger.common.OnUiThread;
import com.github.fge.grappa.debugger.main.MainWindowView;
import com.github.fge.grappa.debugger.trace.tabs.TabPresenter;
import com.github.fge.grappa.debugger.trace.tabs.matches.MatchesTabPresenter;
import com.github.fge.grappa.debugger.trace.tabs.rules.RulesTabPresenter;
import com.github.fge.grappa.debugger.trace.tabs.tree.TreeTabPresenter;
import com.github.fge.grappa.debugger.trace.tabs.treedepth.TreeDepthTabPresenter;
import com.github.fge.grappa.internal.NonFinalForTesting;
import com.github.fge.lambdas.Throwing;
import com.github.fge.lambdas.consumers.ThrowingConsumer;
import com.google.common.annotations.VisibleForTesting;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@NonFinalForTesting
public class TracePresenter
    extends BasePresenter<TraceView>
{
    private final MainWindowView mainView;
    private final GuiTaskRunner taskRunner;
    private final TraceDb traceDb;

    @VisibleForTesting
    final Collection<TabPresenter<?>> tabs = new ArrayList<>();

    public TracePresenter(final MainWindowView mainView,
        final GuiTaskRunner taskRunner, final TraceDb traceDb)
    {
        this.mainView = mainView;
        this.taskRunner = taskRunner;
        this.traceDb = traceDb;
    }

    @Override
    public void load()
    {
        taskRunner.executeBackgroundOrFail(this::pollStatus, this::loadError);
        loadTreeTab();
        loadMatchesTab();
        loadRulesTab();
        loadTreeDepthTab();
    }

    // TODO: delegate this to GuiTaskRunner
    @VisibleForTesting
    @OnBackgroundThread
    void pollStatus()
        throws InterruptedException
    {
        final TraceDbLoadStatus status = traceDb.getLoadStatus();

        if (status.isReady())
            return;

        final ParseInfo info = traceDb.getParseInfo();
        final int total = info.getNrMatchers() + info.getNrNodes();

        taskRunner.executeFront(view::showLoadToolbar);

        while (!status.isReady()) {
            pause();
            final int current
                = status.getLoadedMatchers() + status.getLoadedNodes();
            taskRunner.executeFront(() -> view.reportStatus(total, current));
        }
        taskRunner.executeFront(view::showLoadComplete);
    }

    @VisibleForTesting
    void pause()
        throws InterruptedException
    {
        TimeUnit.SECONDS.sleep(1L);
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
        return new TreeTabPresenter(taskRunner, mainView, traceDb);
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
        return new MatchesTabPresenter(taskRunner, mainView, traceDb);
    }

    public void loadRulesTab()
    {
        final RulesTabPresenter tabPresenter = createRulesTabPresenter();
        view.loadRulesTab(tabPresenter);
        tabPresenter.load();
        tabs.add(tabPresenter);
    }

    @OnUiThread
    @VisibleForTesting
    public RulesTabPresenter createRulesTabPresenter()
    {
        return new RulesTabPresenter(taskRunner, mainView, traceDb);
    }

    public void loadTreeDepthTab()
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
        return new TreeDepthTabPresenter(taskRunner, mainView, traceDb);
    }

    public void handleTabsRefreshEvent()
    {
        taskRunner.run(
            view::disableTabRefresh,
            this::doRefreshTabs,
            this::postTabsRefresh
        );
    }

    @VisibleForTesting
    @OnBackgroundThread
    void doRefreshTabs()
    {
        final ThrowingConsumer<CountDownLatch> await
            = CountDownLatch::await;

        tabs.stream().map(TabPresenter::refresh)
            .forEach(Throwing.consumer(await).orDoNothing());
    }

    @VisibleForTesting
    @OnUiThread
    void postTabsRefresh()
    {
        final TraceDbLoadStatus status = traceDb.getLoadStatus();

        final Runnable runnable = status.isReady()
            ? view::hideLoadToolbar
            : view::enableTabRefresh;

        runnable.run();
    }

    void loadError(final Throwable throwable)
    {
        mainView.showError("Load error", "Unable to load database", throwable);
        view.hideLoadToolbar();
    }


    public void close()
    {
        try {
            traceDb.close();
        } catch (Exception e) {
            mainView.showError("Closing error",
                "Failed to close trace database properly", e);
        }
    }

    @Override
    @Nonnull
    public String toString()
    {
        return traceDb.toString();
    }
}
