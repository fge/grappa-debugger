package com.github.fge.grappa.debugger.csvtrace;

import com.github.fge.grappa.debugger.mainwindow.MainWindowView;
import com.github.fge.grappa.debugger.stats.ParseNode;
import com.github.fge.grappa.internal.NonFinalForTesting;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import javafx.application.Platform;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

@NonFinalForTesting
@ParametersAreNonnullByDefault
public class CsvTracePresenter
{
    private static final ThreadFactory THREAD_FACTORY
        = new ThreadFactoryBuilder().setDaemon(true)
        .setNameFormat("csv-tab-%d").build();

    @VisibleForTesting
    ExecutorService executor
        = Executors.newSingleThreadExecutor(THREAD_FACTORY);

    private final MainWindowView mainView;
    private final CsvTraceModel model;

    private CsvTraceView view;

    public CsvTracePresenter(final MainWindowView mainView,
        final CsvTraceModel model)
    {
        this.mainView = Objects.requireNonNull(mainView);
        this.model = Objects.requireNonNull(model);
    }

    public void setView(final CsvTraceView view)
    {
        this.view = Objects.requireNonNull(view);
    }

    public void loadStats()
    {
        executor.submit(() -> {
            try {
                final ParseNode rootNode = model.getRootNode();
                delayRun(() -> view.loadRootNode(rootNode));
            } catch (IOException e) {
                delayRun(() -> mainView.showError("Parse tree error",
                    "Unable to load parse tree", e));
            }
        });
    }


    void handleExpandParseTree()
    {
        // TODO

    }

    @VisibleForTesting
    void delayRun(final Runnable runnable)
    {
        Platform.runLater(runnable::run);
    }

    public void parseNodeShowEvent(final ParseNode node)
    {
        // TODO

    }
}
