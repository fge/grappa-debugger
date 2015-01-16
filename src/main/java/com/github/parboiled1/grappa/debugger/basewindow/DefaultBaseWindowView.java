package com.github.parboiled1.grappa.debugger.basewindow;

import javafx.scene.Node;

public final class DefaultBaseWindowView
    implements BaseWindowView
{
    private final BaseWindowUi ui;

    public DefaultBaseWindowView(final BaseWindowUi ui)
    {
        this.ui = ui;
    }

    @Override
    public void setWindowContent(final Node pane)
    {
        ui.pane.setCenter(pane);
    }
}
