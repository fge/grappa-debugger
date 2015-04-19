package com.github.fge.grappa.debugger.trace.tabs.treedepth;

import com.github.fge.grappa.debugger.TraceDb;
import com.github.fge.grappa.debugger.common.GuiTaskRunner;
import com.github.fge.grappa.debugger.main.MainWindowView;
import com.github.fge.grappa.debugger.trace.tabs.TabPresenter;
import com.github.fge.grappa.internal.NonFinalForTesting;
import com.google.common.annotations.VisibleForTesting;

import java.util.concurrent.CountDownLatch;

@NonFinalForTesting
public class TreeDepthTabPresenter
    extends TabPresenter<TreeDepthTabView>
{
    private final TreeDepthInfo treeDepthInfo;

    public TreeDepthTabPresenter(final GuiTaskRunner taskRunner,
        final MainWindowView mainView, final TraceDb traceDb)
    {
        super(taskRunner, mainView, traceDb);
        treeDepthInfo = new TreeDepthInfo(info.getNrLines(),
            traceDb.getModel());
    }

    @VisibleForTesting
    TreeDepthTabPresenter(final GuiTaskRunner taskRunner,
        final MainWindowView mainView, final TraceDb traceDb,
        final TreeDepthInfo treeDepthInfo)
    {
        super(taskRunner, mainView, traceDb);
        this.treeDepthInfo = treeDepthInfo;
    }


    @Override
    public CountDownLatch refresh()
    {
        final CountDownLatch latch = new CountDownLatch(1);

        doRefresh(latch);

        return latch;
    }

    @VisibleForTesting
    void doRefresh(final CountDownLatch latch)
    {
        final Runnable runnable = () -> {
            try {
                treeDepthInfo.update();
            } finally {
                latch.countDown();
            }
        };
        doUpdate(runnable);
    }

    @Override
    public void load()
    {
        doUpdate(() -> {});
    }

    public void handleChangeStartLine(final String input)
    {
        if (input == null)
            return;

        try {
            doChangeStartLine(Integer.parseInt(input));
        } catch (NumberFormatException ignored) {
        }
    }

    public void doChangeStartLine(final int startLine)
    {
        doUpdate(() -> treeDepthInfo.setStartLine(startLine));
    }

    public void handlePreviousLines()
    {
        doUpdate(treeDepthInfo::previousLines);
    }

    public void handleNextLines()
    {
        doUpdate(treeDepthInfo::nextLines);
    }

    public void handleChangedDisplayedLines(final int displayedLines)
    {
        doUpdate(() -> treeDepthInfo.setDisplayedLines(displayedLines));
    }

    @VisibleForTesting
    void doUpdate(final Runnable runnable)
    {
        taskRunner.run(view::disableTreeDepthToolbar, runnable,
            () -> view.displayTreeDepthInfo(treeDepthInfo));
    }
}
