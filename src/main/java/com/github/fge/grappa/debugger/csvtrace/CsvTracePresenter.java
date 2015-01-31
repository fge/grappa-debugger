package com.github.fge.grappa.debugger.csvtrace;

import com.github.fge.grappa.buffers.InputBuffer;
import com.github.fge.grappa.debugger.common.BackgroundTaskRunner;
import com.github.fge.grappa.debugger.common.TracePresenter;
import com.github.fge.grappa.debugger.csvtrace.tabs.TreeTabPresenter;
import com.github.fge.grappa.debugger.mainwindow.MainWindowView;
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
    private final InputBuffer buffer;

    public CsvTracePresenter(final MainWindowView mainView,
        final BackgroundTaskRunner taskRunner, final CsvTraceModel model)
        throws IOException
    {
        this.mainView = Objects.requireNonNull(mainView);
        this.taskRunner = taskRunner;
        this.model = Objects.requireNonNull(model);
        buffer = model.getInputBuffer();
    }

    @Override
    public void loadTrace()
    {
        final TreeTabPresenter treeTab = loadTreeTab();
        view.loadTree(treeTab, buffer);
    }

    @VisibleForTesting
    TreeTabPresenter loadTreeTab()
    {
        return new TreeTabPresenter(taskRunner, mainView, model);
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
