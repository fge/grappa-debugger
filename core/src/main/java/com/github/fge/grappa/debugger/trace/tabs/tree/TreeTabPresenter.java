package com.github.fge.grappa.debugger.trace.tabs.tree;

import com.github.fge.grappa.debugger.TraceDb;
import com.github.fge.grappa.debugger.TraceDbLoadStatus;
import com.github.fge.grappa.debugger.common.GuiTaskRunner;
import com.github.fge.grappa.debugger.main.MainWindowView;
import com.github.fge.grappa.debugger.model.TraceModel;
import com.github.fge.grappa.debugger.model.tree.ParseTreeNode;
import com.github.fge.grappa.debugger.trace.tabs.TabPresenter;
import com.google.common.annotations.VisibleForTesting;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public final class TreeTabPresenter
    extends TabPresenter<TreeTabView>
{
    private static final CountDownLatch LATCH = new CountDownLatch(0);

    private final TraceModel model;

    public TreeTabPresenter(final GuiTaskRunner taskRunner,
        final MainWindowView mainView, final TraceDb traceDb)
    {
        super(taskRunner, mainView, traceDb);
        model = traceDb.getModel();
    }


    @Override
    public CountDownLatch refresh()
    {
        return LATCH;
    }

    @Override
    public void load()
    {
        loadInputBuffer();
        loadParseTree();
    }

    @VisibleForTesting
    void loadInputBuffer()
    {
        taskRunner.compute(traceDb::getInputBuffer, view::loadInputBuffer);
    }

    @VisibleForTesting
    void loadParseTree()
    {
        taskRunner.computeOrFail(this::getRootNode, view::displayTree,
            this::handleLoadTreeError);
    }

    @VisibleForTesting
    ParseTreeNode getRootNode()
        throws InterruptedException
    {
        final TraceDbLoadStatus status = traceDb.getLoadStatus();

        while (!status.isReady())
            TimeUnit.SECONDS.sleep(1L);

        return model.getNodeById(0);
    }

    @VisibleForTesting
    void handleLoadTreeError(final Throwable throwable)
    {
        if (!(throwable instanceof InterruptedException))
            mainView.showError("Tree load failure", "Failed to load parse tree",
                throwable);
    }

    public void handleParseTreeNodeShow(final ParseTreeNode node)
    {
        // TODO

    }

    public void handleNeedChildren(final ParseTreeNode value)
    {
        // TODO

    }
}
