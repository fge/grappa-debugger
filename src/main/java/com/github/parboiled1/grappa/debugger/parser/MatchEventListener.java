package com.github.parboiled1.grappa.debugger.parser;

import com.github.parboiled1.grappa.debugger.mainwindow.MainWindowPresenter;
import com.github.parboiled1.grappa.run.MatchFailureEvent;
import com.github.parboiled1.grappa.run.MatchSuccessEvent;
import com.google.common.base.Strings;
import com.google.common.eventbus.Subscribe;
import org.parboiled.MatcherContext;
import org.parboiled.support.Position;

public final class MatchEventListener
{
    private final MainWindowPresenter presenter;

    private int level;
    private String label;
    private int lineNumber;
    private int columnNumber;

    public MatchEventListener(final MainWindowPresenter presenter)
    {
        this.presenter = presenter;
    }

    @Subscribe
    public void failure(final MatchFailureEvent<?> event)
    {
        final MatcherContext<?> context = event.getContext();
        final Position position = context.getPosition();
        level = context.getLevel();
        label = context.getMatcher().toString();
        lineNumber = position.getLine();
        columnNumber = position.getColumn();

        final StringBuilder sb = new StringBuilder("KO: ");
        sb.append(Strings.repeat("  ", level)).append(label)
            .append(" (line ").append(lineNumber)
            .append(", column ").append(columnNumber)
            .append(")\n");
        presenter.addTrace(sb.toString());
    }

    @Subscribe
    public void success(final MatchSuccessEvent<?> event)
    {
        final MatcherContext<?> context = event.getContext();
        final Position position = context.getPosition();
        level = context.getLevel();
        label = context.getMatcher().toString();
        lineNumber = position.getLine();
        columnNumber = position.getColumn();

        final StringBuilder sb = new StringBuilder("OK: ");
        sb.append(Strings.repeat("  ", level)).append(label)
            .append(" (line ").append(lineNumber)
            .append(", column ").append(columnNumber)
            .append(")\n");
        presenter.addTrace(sb.toString());
    }
}
