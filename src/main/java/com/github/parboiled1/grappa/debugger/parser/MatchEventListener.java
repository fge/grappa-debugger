package com.github.parboiled1.grappa.debugger.parser;

import com.github.parboiled1.grappa.debugger.mainwindow.MainWindowPresenter;
import com.github.parboiled1.grappa.run.PreMatchEvent;
import com.google.common.eventbus.Subscribe;
import org.parboiled.MatcherContext;

public final class MatchEventListener
{
    private final MainWindowPresenter presenter;

    public MatchEventListener(final MainWindowPresenter presenter)
    {
        this.presenter = presenter;
    }

    @Subscribe
    public void preMatch(final PreMatchEvent<?> event)
    {
        final StringBuilder sb = new StringBuilder("TRYING: ");
        final MatcherContext<?> context = event.getContext();
        sb.append(context.getPath())
            .append(' ').append(context.getPosition())
            .append('\n');
        presenter.addTrace(sb.toString());
    }
}
