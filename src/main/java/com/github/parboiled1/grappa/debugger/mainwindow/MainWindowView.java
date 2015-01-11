package com.github.parboiled1.grappa.debugger.mainwindow;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface MainWindowView
{
    void setInputText(String inputText);

    void closeWindow();

    void addTraceText(String trace);

    String getInputText();

    void fillTree();
}
