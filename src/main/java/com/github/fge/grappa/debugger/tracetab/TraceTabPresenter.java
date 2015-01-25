package com.github.fge.grappa.debugger.tracetab;

import com.github.fge.grappa.buffers.InputBuffer;
import com.github.fge.grappa.debugger.mainwindow.MainWindowView;
import com.github.fge.grappa.debugger.statistics.ParseNode;
import com.github.fge.grappa.debugger.statistics.StatsType;
import com.github.fge.grappa.debugger.tracetab.stat.global.GlobalStatsPresenter;
import com.google.common.annotations.VisibleForTesting;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.util.Objects;

@ParametersAreNonnullByDefault
public class TraceTabPresenter
{
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

    public void handleLoadStats(final StatsType newValue)
    {
        switch (newValue) {
            case GLOBAL:
                try {
                    loadGlobalStats();
                } catch (IOException e) {
                    parentView.showError("Statistics loading error",
                        "Unable to load statistics", e);
                }
                break;
            default:
                throw new UnsupportedOperationException(newValue
                    + " not supported yet");
        }
    }

    @VisibleForTesting
    GlobalStatsPresenter getGlobalStatsPresenter()
    {
        return new GlobalStatsPresenter(model);
    }

    @VisibleForTesting
    void loadGlobalStats()
        throws IOException
    {
        final GlobalStatsPresenter presenter = getGlobalStatsPresenter();
        view.loadGlobalStats(presenter);
    }
}
