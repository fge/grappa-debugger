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
import com.github.fge.grappa.debugger.trace.tabs.tree.TreeTabPresenter;
import com.github.fge.grappa.internal.NonFinalForTesting;
import com.github.fge.lambdas.Throwing;
import com.google.common.annotations.VisibleForTesting;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
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
        final Runnable runnable = Throwing.runnable(this::pollStatus)
            .orDoNothing();
        taskRunner.executeBackground(runnable);
        loadTreeTab();
    }

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
            TimeUnit.SECONDS.sleep(1L);
            final int current
                = status.getLoadedMatchers() + status.getLoadedNodes();
            taskRunner.executeFront(() -> view.reportStatus(total, current));
        }
        taskRunner.executeFront(view::showLoadComplete);
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

    @Override
    @Nonnull
    public String toString()
    {
        return traceDb.toString();
    }

    public void handleTabsRefreshEvent()
    {
        // TODO
    }
}
