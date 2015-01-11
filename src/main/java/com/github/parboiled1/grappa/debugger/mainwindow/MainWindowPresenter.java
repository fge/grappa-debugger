package com.github.parboiled1.grappa.debugger.mainwindow;

import javax.annotation.ParametersAreNonnullByDefault;

// Non final for testing
@ParametersAreNonnullByDefault
public class MainWindowPresenter
{
    // TODO: maybe UI is unnecessary in this class?
    private final MainWindowUi ui;
    private final MainWindowView view;

    public MainWindowPresenter(final MainWindowUi ui, final MainWindowView view)
    {
        this.ui = ui;
        this.view = view;
    }

    public void loadInput()
    {
        view.setInputText("hello");
    }

    public void closeWindow()
    {
        view.closeWindow();
    }
}
