package com.github.fge.grappa.debugger;

import com.github.fge.grappa.debugger.alert.AlertFactory;
import com.github.fge.grappa.debugger.mainwindow.JavafxMainWindowView;
import com.github.fge.grappa.debugger.mainwindow.MainWindowDisplay;
import com.github.fge.grappa.debugger.mainwindow.MainWindowPresenter;
import com.github.fge.grappa.debugger.mainwindow.MainWindowView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import javax.annotation.Nullable;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public final class GrappaDebugger
    extends Application
    implements MainWindowFactory
{
    private static final URL BASE_WINDOW_FXML;

    static {
        BASE_WINDOW_FXML = GrappaDebugger.class.getResource("/mainWindow.fxml");
        if (BASE_WINDOW_FXML == null)
            throw new ExceptionInInitializerError("unable to load base window"
                + " fxml");
    }

    private final AlertFactory alertFactory = new AlertFactory();

    private final Map<MainWindowPresenter, Stage> windows = new HashMap<>();

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
    @Nullable
    public MainWindowPresenter createWindow()
    {
        return createWindow(new Stage());
    }

    private MainWindowPresenter createWindow(final Stage stage)
    {
        final FXMLLoader loader = new FXMLLoader(BASE_WINDOW_FXML);
        final Pane pane;
        try {
            pane = loader.load();
        } catch (IOException e) {
            alertFactory.showError("Window creation error",
                "Unable to create window", e);
            return null;
        }

        final MainWindowDisplay ui = loader.getController();
        final MainWindowView view
            = new JavafxMainWindowView(stage, alertFactory, ui);
        final MainWindowPresenter presenter
            = new MainWindowPresenter(this, view);

        ui.init(presenter);

        stage.setScene(new Scene(pane));
        stage.setTitle("Grappa debugger");

        windows.put(presenter, stage);

        stage.show();

        return presenter;
    }

    @Override
    public void close(final MainWindowPresenter presenter)
    {
        windows.remove(presenter).close();
    }
}
