package com.github.parboiled1.grappa.debugger;

import com.github.parboiled1.grappa.debugger.alert.AlertFactory;
import com.github.parboiled1.grappa.debugger.basewindow.BaseWindowPresenter;
import com.github.parboiled1.grappa.debugger.basewindow.BaseWindowUi;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public final class DefaultBaseWindowFactory
    implements BaseWindowFactory
{
    private static final AlertFactory ALERT_FACTORY = new AlertFactory();
    private static final URL MAIN_WINDOW_FXML;

    static {
        try {
            MAIN_WINDOW_FXML = DefaultBaseWindowFactory.class.getResource(
                "/baseWindow.fxml");
            if (MAIN_WINDOW_FXML == null)
                throw new IOException("base window fxml not found");
        } catch (IOException e) {
            ALERT_FACTORY.showError("Fatal error", "cannot load base FXML", e);
            throw new ExceptionInInitializerError(e);
        }
    }

    private final Map<BaseWindowPresenter, Stage> windows
        = new HashMap<>();

    private final AtomicInteger windowCount = new AtomicInteger();

    @Override
    public void createWindow(final Stage stage)
    {
        final FXMLLoader loader = new FXMLLoader(MAIN_WINDOW_FXML);
        final Pane pane;
        try {
            pane = loader.load();
        } catch (IOException e) {
            ALERT_FACTORY.showError("Fatal error", "unable to create window",
                e);
            return;
        }

        final BaseWindowPresenter presenter = new BaseWindowPresenter(this);

        final BaseWindowUi ui = loader.getController();
        ui.init(presenter);

        stage.setScene(new Scene(pane));
        stage.setTitle("window " + windowCount.getAndIncrement());

        windows.put(presenter, stage);

        stage.show();
    }

    @Override
    public void close(final BaseWindowPresenter presenter)
    {
        windows.get(presenter).close();
    }
}
