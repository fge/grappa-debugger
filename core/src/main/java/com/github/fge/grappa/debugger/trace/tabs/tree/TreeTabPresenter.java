package com.github.fge.grappa.debugger.trace.tabs.tree;

import com.github.fge.grappa.debugger.TraceDb;
import com.github.fge.grappa.debugger.TraceDbLoadStatus;
import com.github.fge.grappa.debugger.common.GuiTaskRunner;
import com.github.fge.grappa.debugger.main.MainWindowView;
import com.github.fge.grappa.debugger.model.TraceModel;
import com.github.fge.grappa.debugger.model.tree.ParseTreeNode;
import com.github.fge.grappa.debugger.trace.tabs.TabPresenter;
import com.github.fge.grappa.internal.NonFinalForTesting;
import com.google.common.annotations.VisibleForTesting;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@NonFinalForTesting
public class TreeTabPresenter
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
            showError("Tree load failure", "Failed to load parse tree",
                throwable);
    }

    public void handleParseTreeNodeShow(final ParseTreeNode node)
    {
        final int end = node.getEndIndex();
        view.showParseTreeNode(node);
        if (node.isSuccess())
            view.highlightSuccess(node.getStartIndex(), end);
        else
            view.highlightFailure(end);
    }

    public void handleNeedChildren(final ParseTreeNode value)
    {
        taskRunner.compute(
            view::waitForChildren,
            () -> model.getNodeChildren(value.getId()),
            view::setTreeChildren
        );
    }
}
