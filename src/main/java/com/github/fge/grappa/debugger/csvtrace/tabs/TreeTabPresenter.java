package com.github.fge.grappa.debugger.csvtrace.tabs;

import com.github.fge.grappa.debugger.common.BackgroundTaskRunner;
import com.github.fge.grappa.debugger.common.BasePresenter;
import com.github.fge.grappa.debugger.csvtrace.CsvTraceModel;
import com.github.fge.grappa.debugger.mainwindow.MainWindowView;
import com.github.fge.grappa.debugger.stats.ParseNode;
import com.google.common.annotations.VisibleForTesting;

import javax.annotation.ParametersAreNonnullByDefault;
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
        loadParseTree();
        loadInputText();
        loadParseRunInfo();
    }

    @VisibleForTesting
    void loadParseTree()
    {
        taskRunner.computeOrFail(model::getRootNode, view::loadTree,
            throwable -> mainView.showError("Parse tree error",
                "Unable to load parse tree", throwable));
    }

    @VisibleForTesting
    void loadInputText()
    {
        view.loadText();
    }

    @VisibleForTesting
    void handleParseNodeShow(final ParseNode node)
    {
        final int end = node.getEnd();
        view.showParseNode(node);
        if (node.isSuccess())
            view.highlightSuccess(node.getStart(), end);
        else
            view.highlightFailure(end);
    }

    @VisibleForTesting
    void loadParseRunInfo()
    {
        taskRunner.computeOrFail(
            model::getParseRunInfo,
            view::loadParseRunInfo,
            throwable -> mainView.showError("Trace load failure",
                "Unable to load parse run info", throwable)
        );
    }
}
