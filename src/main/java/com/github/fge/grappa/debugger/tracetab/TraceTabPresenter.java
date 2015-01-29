package com.github.fge.grappa.debugger.tracetab;

import com.github.fge.grappa.debugger.common.BackgroundTaskRunner;
import com.github.fge.grappa.debugger.common.BasePresenter;
import com.github.fge.grappa.debugger.stats.ParseNode;
import com.github.fge.grappa.debugger.tracetab.stat.classdetails.ClassDetailsStatsModel;
import com.github.fge.grappa.debugger.tracetab.stat.classdetails.ClassDetailsStatsPresenter;
import com.github.fge.grappa.debugger.tracetab.stat.classdetails.DefaultClassDetailsStatsModel;
import com.github.fge.grappa.debugger.tracetab.stat.global.DefaultGlobalStatsModel;
import com.github.fge.grappa.debugger.tracetab.stat.global.GlobalStatsModel;
import com.github.fge.grappa.debugger.tracetab.stat.global.GlobalStatsPresenter;
import com.google.common.annotations.VisibleForTesting;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@ParametersAreNonnullByDefault
public class TraceTabPresenter
    extends BasePresenter<TraceTabView>
{
    private final BackgroundTaskRunner taskRunner;
    private final TraceTabModel model;

    public TraceTabPresenter(final BackgroundTaskRunner taskRunner,
        final TraceTabModel model)
    {
        this.taskRunner = taskRunner;
        this.model = Objects.requireNonNull(model);
    }

    public void loadTrace()
    {
        view.setInputText(model.getInputBuffer());
        view.setInfo(model.getInfo());
        view.setEvents(model.getEvents());
        view.setParseTree(model.getRootNode());
        taskRunner.run(this::getGlobalStatsPresenter, view::loadGlobalStats);
        taskRunner.run(this::getClassDetailsStatsPresenter,
            view::loadClassDetailsStats);
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

    @VisibleForTesting
    GlobalStatsPresenter getGlobalStatsPresenter()
    {
        final GlobalStatsModel statsModel = new DefaultGlobalStatsModel(model);
        return new GlobalStatsPresenter(statsModel);
    }

    @VisibleForTesting
    ClassDetailsStatsPresenter getClassDetailsStatsPresenter()
    {
        final ClassDetailsStatsModel statsModel
            = new DefaultClassDetailsStatsModel(model);
        return new ClassDetailsStatsPresenter(statsModel);
    }

}
