package com.github.fge.grappa.debugger.csvtrace.tabs.tree;

import com.github.fge.grappa.debugger.common.BackgroundTaskRunner;
import com.github.fge.grappa.debugger.common.BasePresenter;
import com.github.fge.grappa.debugger.csvtrace.CsvTraceModel;
import com.github.fge.grappa.debugger.csvtrace.newmodel.ParseTreeNode;
import com.github.fge.grappa.debugger.mainwindow.MainWindowView;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Objects;

@ParametersAreNonnullByDefault
public class TreeTabPresenter
    extends BasePresenter<TreeTabView>
{
    private final CsvTraceModel model;
    private final BackgroundTaskRunner taskRunner;
    private final MainWindowView mainView;

    public TreeTabPresenter(final BackgroundTaskRunner taskRunner,
        final MainWindowView mainView, final CsvTraceModel model)
    {
        this.taskRunner = Objects.requireNonNull(taskRunner);
        this.mainView = Objects.requireNonNull(mainView);
        this.model = Objects.requireNonNull(model);
    }

    public void load()
    {
        loadInputText();
        loadParseTree();
    }

    void loadInputText()
    {
        taskRunner.computeOrFail(
            model::getInputText,
            view::loadInputText,
            throwable -> mainView.showError("Text loading error",
                "Unable to load input text", throwable)
        );
    }

    void loadParseTree()
    {
        taskRunner.computeOrFail(
            model::getParseTree,
            view::loadParseTree,
            throwable -> mainView.showError("Tree load failure",
                "Unable to load parse tree", throwable)
        );
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
    {
        return model.getNodeChildren(nodeId);
    }
}
