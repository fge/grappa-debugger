package com.github.fge.grappa.debugger.csvtrace;

import com.github.fge.grappa.debugger.common.BackgroundTaskRunner;
import com.github.fge.grappa.debugger.common.TracePresenter;
import com.github.fge.grappa.debugger.csvtrace.tabs.TreeTabPresenter;
import com.github.fge.grappa.debugger.mainwindow.MainWindowView;
import com.github.fge.grappa.debugger.stats.ParseNode;
import com.github.fge.grappa.internal.NonFinalForTesting;
import com.google.common.annotations.VisibleForTesting;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.util.Objects;

@NonFinalForTesting
@ParametersAreNonnullByDefault
public class CsvTracePresenter
    extends TracePresenter<CsvTraceView>
{
    private final MainWindowView mainView;
    private final BackgroundTaskRunner taskRunner;
    private final CsvTraceModel model;

    public CsvTracePresenter(final MainWindowView mainView,
        final BackgroundTaskRunner taskRunner, final CsvTraceModel model)
    {
        this.mainView = Objects.requireNonNull(mainView);
        this.taskRunner = taskRunner;
        this.model = Objects.requireNonNull(model);
    }

    @Override
    public void loadTrace()
    {
//        taskRunner.computeOrFail(model::getRootNode, view::loadRootNode,
//            e -> mainView.showError("Parse tree error",
//                "Unable to load parse tree", e));
        taskRunner.computeOrFail(this::loadTreeTab, view::loadTree,
            e -> mainView.showError("Parse tree error",
                "Unable to load parse tree", e));
    }

    @VisibleForTesting
    TreeTabPresenter loadTreeTab()
        throws IOException
    {
        return new TreeTabPresenter(model.getRootNode());
    }

    void handleExpandParseTree()
    {
    }

    public void parseNodeShowEvent(final ParseNode node)
    {
    }

    @Override
    public void dispose()
    {
        try {
            model.dispose();
        } catch (IOException e) {
            mainView.showError("Trace file error", "Problem closing trace file",
                e);
        }
    }
}
