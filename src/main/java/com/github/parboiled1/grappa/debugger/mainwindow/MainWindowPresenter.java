package com.github.parboiled1.grappa.debugger.mainwindow;

import javax.annotation.ParametersAreNonnullByDefault;

// Non final for testing
@ParametersAreNonnullByDefault
public class MainWindowPresenter
{
    // TODO: maybe UI is unnecessary in this class?
    private final MainWindowUi ui;
    private final MainWindowView view;
    private final MainWindowModel model;

    public MainWindowPresenter(final MainWindowUi ui, final MainWindowView view,
        final MainWindowModel model)
    {
        this.ui = ui;
        this.view = view;
        this.model = model;
    }

    public void loadInput()
    {
        view.setInputText("hello");
    }

    public void closeWindow()
    {
        view.closeWindow();
    }

    public void addTrace(final String trace)
    {
        view.addTrace(trace);
    }

    public void runTrace()
    {
        final String text = view.getInputText();
        model.trace(this, text);
    }
}
