package com.github.parboiled1.grappa.debugger.mainwindow;

import javafx.scene.control.TreeItem;

public interface MainWindowModel
{
    TreeItem<String> runTrace(String inputText);
}
