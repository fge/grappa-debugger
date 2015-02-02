package com.github.fge.grappa.debugger.csvtrace;

import com.github.fge.grappa.debugger.common.BackgroundTaskRunner;
import com.github.fge.grappa.debugger.common.TracePresenter;
import com.github.fge.grappa.debugger.csvtrace.tabs.tree.TreeTabPresenter;
import com.github.fge.grappa.debugger.mainwindow.MainWindowView;
import com.github.fge.grappa.internal.NonFinalForTesting;

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
        final TreeTabPresenter treeTab
            = new TreeTabPresenter(taskRunner, mainView, model);
        view.loadTreeTab(treeTab);
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
