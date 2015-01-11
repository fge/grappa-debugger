package com.github.parboiled1.grappa.debugger.mainwindow;

import com.github.parboiled1.grappa.debugger.parser.MatchEventListener;
import com.github.parboiled1.grappa.debugger.parser.TestParser;
import com.github.parboiled1.grappa.run.EventBasedParseRunner;
import org.parboiled.Parboiled;

public final class DefaultMainWindowModel
    implements MainWindowModel
{
    @Override
    public void trace(final MainWindowPresenter presenter,
        final String inputText)
    {
        final TestParser parser = Parboiled.createParser(TestParser.class);
        final EventBasedParseRunner<String> runner
            = new EventBasedParseRunner<>(parser.quotedString());
        runner.registerListener(new MatchEventListener(presenter));
        runner.run(inputText);
    }
}
