package com.github.parboiled1.grappa.debugger.mainwindow;

import com.github.parboiled1.grappa.debugger.parser.MatchResult;
import javafx.scene.control.TreeItem;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface MainWindowView
{
    void setInputText(String inputText);

    String getInputText();

    void setParseTree(TreeItem<MatchResult> root);

    void setTraceDetail(String text);

    void closeWindow();

    void highlightMatch(int start, int end);
}
