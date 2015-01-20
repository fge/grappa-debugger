package com.github.fge.grappa.debugger;

import com.github.fge.grappa.debugger.alert.AlertFactory;
import com.github.fge.grappa.debugger.basewindow.BaseWindowPresenter;
import com.github.fge.grappa.debugger.basewindow.BaseWindowUi;
import com.github.fge.grappa.debugger.basewindow.BaseWindowView;
import com.github.fge.grappa.debugger.basewindow.DefaultBaseWindowView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public final class GrappaDebugger
    extends Application
    implements BaseWindowFactory
{
    private static final URL BASE_WINDOW_FXML;

    static {
        BASE_WINDOW_FXML = GrappaDebugger.class.getResource("/baseWindow.fxml");
        if (BASE_WINDOW_FXML == null)
            throw new ExceptionInInitializerError("unable to load base window"
                + " fxml");
    }

    private final AlertFactory alertFactory = new AlertFactory();

    private final Map<BaseWindowPresenter, Stage> windows = new HashMap<>();

    @Override
    public void start(final Stage primaryStage)
    {
        createWindow(primaryStage);
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            if (Platform.isFxApplicationThread())
                alertFactory.unhandledError(e);
            else
                e.printStackTrace(System.err);
        });
    }

    public static void main(final String... args)
    {
        launch(args);
    }

    @Override
    public void createWindow()
    {
        createWindow(new Stage());
    }

    private void createWindow(final Stage stage)
    {
        final FXMLLoader loader = new FXMLLoader(BASE_WINDOW_FXML);
        final Pane pane;
        try {
            pane = loader.load();
        } catch (IOException e) {
            alertFactory.showError("Window creation error",
                "Unable to create window", e);
            return;
        }

        final BaseWindowUi ui = loader.getController();
        final BaseWindowView view
            = new DefaultBaseWindowView(stage, alertFactory, ui);
        final BaseWindowPresenter presenter
            = new BaseWindowPresenter(this, view);

        ui.init(presenter);

        stage.setScene(new Scene(pane));
        stage.setTitle("Grappa debugger");

        windows.put(presenter, stage);

        stage.show();
    }

    @Override
    public void close(final BaseWindowPresenter presenter)
    {
        windows.remove(presenter).close();
    }

}
