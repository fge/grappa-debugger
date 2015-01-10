package com.github.parboiled1.grappa.debugger.presenter;

import com.github.parboiled1.grappa.debugger.view.GrappaDebuggerView;

public final class GrappaDebuggerPresenter
{
    private final GrappaDebuggerView view;

    public GrappaDebuggerPresenter(final GrappaDebuggerView view)
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
