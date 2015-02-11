package com.github.fge.grappa.debugger.csvtrace.tabs.tree;

import com.github.fge.grappa.debugger.GrappaDebuggerException;
import com.github.fge.grappa.debugger.common.GuiTaskRunner;
import com.github.fge.grappa.debugger.csvtrace.CsvTraceModel;
import com.github.fge.grappa.debugger.javafx.TabPresenter;
import com.github.fge.grappa.debugger.mainwindow.MainWindowView;
import com.github.fge.grappa.debugger.model.ParseTreeNode;
import com.google.common.annotations.VisibleForTesting;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

@ParametersAreNonnullByDefault
public class TreeTabPresenter
    extends TabPresenter<TreeTabView>
{
    private final CsvTraceModel model;
    private final GuiTaskRunner taskRunner;
    private final MainWindowView mainView;

    public TreeTabPresenter(final GuiTaskRunner taskRunner,
        final MainWindowView mainView, final CsvTraceModel model)
    {
        this.taskRunner = Objects.requireNonNull(taskRunner);
        this.mainView = Objects.requireNonNull(mainView);
        this.model = Objects.requireNonNull(model);
    }

    @Override
    public void load()
    {
        loadInputText();
        loadParseTree();
    }

    @Override
    public CountDownLatch refresh()
    {
        return new CountDownLatch(0);
    }

    @VisibleForTesting
    void loadInputText()
    {
        taskRunner.computeOrFail(model::getInputText, view::loadInputText,
            this::handleLoadInputTextError);
    }

    @VisibleForTesting
    void handleLoadInputTextError(final Throwable throwable)
    {
        mainView.showError("Text loading error", "Unable to load input text",
            throwable);
    }

    @VisibleForTesting
    void loadParseTree()
    {
        taskRunner.computeOrFail(model::getParseTree, view::loadParseTree,
            this::handleLoadParseTreeError);
    }

    @VisibleForTesting
    void handleLoadParseTreeError(final Throwable throwable)
    {
        mainView.showError("Tree load failure", "Unable to load parse tree",
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

    public List<ParseTreeNode> getNodeChildren(final int nodeId)
        throws GrappaDebuggerException
    {
        return model.getNodeChildren(nodeId);
    }

    public void handleNeedChildren(final ParseTreeNode value)
    {
        taskRunner.computeOrFail(
            view::waitForChildren,
            () -> getNodeChildren(value.getId()),
            view::setTreeChildren,
            throwable -> mainView.showError("Tree expand error",
                "Unable to extend parse tree node", throwable)
        );
    }
}
