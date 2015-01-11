package com.github.parboiled1.grappa.debugger.mainwindow;

import javafx.scene.control.TreeItem;

public interface MainWindowModel
{
    void trace(MainWindowPresenter presenter, String inputText);

    TreeItem<String> runTrace(String inputText);
}
