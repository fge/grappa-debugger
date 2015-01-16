package com.github.parboiled1.grappa.debugger.tracetab;

public class TraceTabPresenter
{
    private final TraceTabModel model;
    private TraceTabView view;

    public TraceTabPresenter(final TraceTabModel model)
    {
        this.model = model;
    }

    public void setView(final TraceTabView view)
    {
        this.view = view;
    }
}
