package com.github.parboiled1.grappa.debugger.mainwindow;

import com.github.parboiled1.grappa.debugger.parser.MatchResult;
import javafx.scene.control.TreeItem;

public interface MainWindowModel
{
    TreeItem<MatchResult> runTrace(String inputText);
}
