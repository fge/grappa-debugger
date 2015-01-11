package com.github.parboiled1.grappa.debugger.mainwindow;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface MainWindowView
{
    void setInputText(String inputText);

    void closeWindow();

    void addTrace(String trace);

    String getInputText();
}
