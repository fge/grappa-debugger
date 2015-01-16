package com.github.parboiled1.grappa.debugger.basewindow;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

import java.io.IOException;
import java.net.URL;

public final class DefaultBaseWindowView
    implements BaseWindowView
{
    private final BaseWindowUi ui;
    private final URL traceTabFxml;

    public DefaultBaseWindowView(final BaseWindowUi ui)
    {
        this.ui = ui;
        traceTabFxml = BaseWindowPresenter.class.getResource("/traceTab.fxml");
        if (traceTabFxml == null)
            throw new RuntimeException("cannot load tab fxml");
    }

    @Override
    public void injectTab()
    {
        final FXMLLoader loader = new FXMLLoader(traceTabFxml);
        final Node pane;
        try {
            pane = loader.load();
        } catch (IOException oops) {
            throw new RuntimeException("cannot create tab", oops);
        }
        ui.pane.setCenter(pane);
    }
}
