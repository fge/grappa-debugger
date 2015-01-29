package com.github.fge.grappa.debugger;

import com.github.fge.grappa.debugger.common.BackgroundTaskRunner;
import com.github.fge.grappa.debugger.javafx.AlertFactory;
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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
    private final BackgroundTaskRunner taskRunner
        = new BackgroundTaskRunner("grappa-debugger-%d", Platform::runLater);

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

    @Nullable
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

        final MainWindowDisplay display = loader.getController();
        final MainWindowView view
            = new JavafxMainWindowView(stage, alertFactory, display);
        final MainWindowPresenter presenter
            = new MainWindowPresenter(this, taskRunner);
        presenter.setView(view);

        display.setPresenter(presenter);

        stage.setScene(new Scene(pane, 1024, 768));
        stage.setTitle("Grappa debugger");

        windows.put(presenter, stage);

        stage.show();

        return presenter;
    }

    @Override
    public void stop()
    {
        taskRunner.dispose();
    }

    @Override
    public void close(@Nonnull final MainWindowPresenter presenter)
    {
        Objects.requireNonNull(presenter);
        windows.remove(presenter).close();
    }
}
