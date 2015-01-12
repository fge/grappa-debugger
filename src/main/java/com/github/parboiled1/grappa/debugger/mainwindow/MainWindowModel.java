package com.github.parboiled1.grappa.debugger.mainwindow;

import com.github.parboiled1.grappa.debugger.parser.ParsingRunResult;

public interface MainWindowModel
{
    ParsingRunResult runTrace(String inputText);
}
