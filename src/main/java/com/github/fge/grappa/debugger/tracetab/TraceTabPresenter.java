package com.github.fge.grappa.debugger.tracetab;

import com.github.fge.grappa.buffers.InputBuffer;
import com.github.fge.grappa.debugger.mainwindow.MainWindowView;
import com.github.fge.grappa.debugger.stats.ParseNode;
import com.github.fge.grappa.debugger.stats.StatsType;
import com.github.fge.grappa.debugger.tracetab.stat.classdetails.ClassDetailsStatsModel;
import com.github.fge.grappa.debugger.tracetab.stat.classdetails.ClassDetailsStatsPresenter;
import com.github.fge.grappa.debugger.tracetab.stat.classdetails.DefaultClassDetailsStatsModel;
import com.github.fge.grappa.debugger.tracetab.stat.global.DefaultGlobalStatsModel;
import com.github.fge.grappa.debugger.tracetab.stat.global.GlobalStatsModel;
import com.github.fge.grappa.debugger.tracetab.stat.global.GlobalStatsPresenter;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import javafx.application.Platform;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

@ParametersAreNonnullByDefault
public class TraceTabPresenter
{
    private static final ThreadFactory THREAD_FACTORY
        = new ThreadFactoryBuilder().setDaemon(true)
        .setNameFormat("tab-loader-%d").build();

    private final ExecutorService executor
        = Executors.newSingleThreadExecutor(THREAD_FACTORY);

    private final TraceTabModel model;
    private final InputBuffer buffer;

    private final MainWindowView parentView;

    private TraceTabView view;

    public TraceTabPresenter(final MainWindowView parentView,
        final TraceTabModel model)
    {
        this.parentView = parentView;
        this.model = Objects.requireNonNull(model);
        buffer = model.getInputBuffer();
    }

    public void setView(final TraceTabView view)
    {
        this.view = Objects.requireNonNull(view);
    }

    public void loadTrace()
    {
        view.setInputText(model.getInputBuffer());
        view.setInfo(model.getInfo());
        view.setEvents(model.getEvents());
        view.setParseTree(model.getRootNode());
    }

    public void handleExpandParseTree()
    {
        view.expandParseTree();
    }

    public void handleParseNodeShow(final ParseNode node)
    {
        view.showParseNode(Objects.requireNonNull(node));
        if (node.isSuccess())
            view.highlightSuccessfulMatch(node.getStart(), node.getEnd());
        else
            view.highlightFailedMatch(node.getEnd());
    }

    public void handleLoadStats(final StatsType type)
    {
        switch (type) {
            case GLOBAL:
                loadGlobalStats();
                break;
            case CLASS_DETAILS:
                loadClassDetailsStats();
                break;
            default:
                throw new UnsupportedOperationException(type + " not "
                    + "supported yet");
        }
    }

    @VisibleForTesting
    GlobalStatsPresenter getGlobalStatsPresenter()
    {
        final GlobalStatsModel statsModel = new DefaultGlobalStatsModel(model);
        return new GlobalStatsPresenter(statsModel);
    }

    @VisibleForTesting
    void loadGlobalStats()
    {
        executor.submit(() -> {
            final GlobalStatsPresenter presenter = getGlobalStatsPresenter();
            Platform.runLater(() -> view.loadGlobalStats(presenter));
        });
    }

    @VisibleForTesting
    ClassDetailsStatsPresenter getClassDetailsStatsPresenter()
    {
        final ClassDetailsStatsModel statsModel
            = new DefaultClassDetailsStatsModel(model);
        return new ClassDetailsStatsPresenter(statsModel);
    }

    @VisibleForTesting
    void loadClassDetailsStats()
    {
        executor.submit(() -> {
            final ClassDetailsStatsPresenter presenter
                = getClassDetailsStatsPresenter();
            Platform.runLater(() -> view.loadClassDetailsStats(presenter));
        });
    }
}
