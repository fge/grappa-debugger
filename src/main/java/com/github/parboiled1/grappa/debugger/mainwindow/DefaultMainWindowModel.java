package com.github.parboiled1.grappa.debugger.mainwindow;

import com.github.parboiled1.grappa.debugger.parser.MatchResult;
import com.github.parboiled1.grappa.debugger.parser.ParseListener;
import com.github.parboiled1.grappa.debugger.parser.TestParser;
import com.github.parboiled1.grappa.run.EventBasedParseRunner;
import javafx.scene.control.TreeItem;
import org.parboiled.Parboiled;

public final class DefaultMainWindowModel
    implements MainWindowModel
{
    @Override
    public TreeItem<MatchResult> runTrace(final String inputText)
    {
        final TestParser parser = Parboiled.createParser(TestParser.class);
        final EventBasedParseRunner<String> runner
            = new EventBasedParseRunner<>(parser.quotedString());
        final ParseListener listener = new ParseListener();
        runner.registerListener(listener);
        runner.run(inputText);
        return listener.getRoot();
    }
}
