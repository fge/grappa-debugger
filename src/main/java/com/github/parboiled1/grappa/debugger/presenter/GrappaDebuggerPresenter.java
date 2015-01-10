package com.github.parboiled1.grappa.debugger.presenter;

import com.github.parboiled1.grappa.debugger.view.GrappaDebuggerView;

public final class GrappaDebuggerPresenter
{
    private GrappaDebuggerView view;

    public GrappaDebuggerPresenter()
    {
    }

    public GrappaDebuggerPresenter(final GrappaDebuggerView view)
    {
        this.view = view;
    }

    // TODO! Find a way NOT to have that
    public void setView(final GrappaDebuggerView view)
    {
        this.view = view;
    }

    public void display()
    {
        view.display();
    }

    public void close()
    {
        view.close();
    }
}
