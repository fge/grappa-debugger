package com.github.parboiled1.grappa.debugger.mainwindow;

import javafx.scene.control.TreeItem;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface MainWindowView
{
    void setInputText(String inputText);

    String getInputText();

    void setParseTree(TreeItem<String> root);

    void setTraceDetail(String text);

    void closeWindow();
}
