package com.github.fge.grappa.debugger.tracetab;

import com.github.fge.grappa.buffers.InputBuffer;
import com.github.fge.grappa.debugger.statistics.ParseNode;

public class TraceTabPresenter
{
    private final TraceTabModel model;
    private final InputBuffer buffer;

    private TraceTabView view;

    public TraceTabPresenter(final TraceTabModel model)
    {
        this.model = model;
        buffer = model.getInputBuffer();
    }

    public void setView(final TraceTabView view)
    {
        this.view = view;
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
        view.showParseNode(node);
        if (node.isSuccess())
            view.highlightSuccessfulMatch(node.getStart(), node.getEnd());
        else
            view.highlightFailedMatch(node.getEnd());
    }
}
