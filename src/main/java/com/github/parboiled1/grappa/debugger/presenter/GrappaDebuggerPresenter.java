package com.github.parboiled1.grappa.debugger.presenter;

import com.github.parboiled1.grappa.debugger.view.GrappaDebuggerView;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@ParametersAreNonnullByDefault
public final class GrappaDebuggerPresenter
{
    private GrappaDebuggerView view;

    public GrappaDebuggerPresenter()
    {
    }

    public GrappaDebuggerPresenter(final GrappaDebuggerView view)
    {
        this.view = Objects.requireNonNull(view);
        initializeView();
    }

    // TODO! Find a way NOT to have that
    public void setView(final GrappaDebuggerView view)
    {
        if (this.view != null)
            throw new IllegalStateException();

        this.view = Objects.requireNonNull(view);
        initializeView();
    }

    public void display()
    {
        view.display();
    }

    public void close()
    {
        view.close();
    }

    private void initializeView()
    {
        view.setButtonAction(event -> System.out.println("meh"));
    }
}
