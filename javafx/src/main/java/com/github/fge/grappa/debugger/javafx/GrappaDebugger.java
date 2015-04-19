package com.github.fge.grappa.debugger.javafx;

import com.github.fge.grappa.debugger.MainWindowFactory2;
import com.github.fge.grappa.debugger.ZipTraceDbFactory;
import com.github.fge.grappa.debugger.common.GuiTaskRunner;
import com.github.fge.grappa.debugger.h2.db.H2TraceDbFactory;
import com.github.fge.grappa.debugger.javafx.common.AlertFactory;
import com.github.fge.grappa.debugger.javafx.main.JavafxMainWindowView;
import com.github.fge.grappa.debugger.main.MainWindowPresenter;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@ParametersAreNonnullByDefault
public final class GrappaDebugger
    extends Application
    implements MainWindowFactory2
{
    private static final URL BASE_WINDOW_FXML;
    private static final URL MATCH_HIGHLIGHT_CSS;

    static {
        final Class<GrappaDebugger> me = GrappaDebugger.class;
        BASE_WINDOW_FXML = me.getResource("/mainWindow.fxml");
        if (BASE_WINDOW_FXML == null)
            throw new ExceptionInInitializerError("unable to load base window"
                + " fxml");
        MATCH_HIGHLIGHT_CSS = me.getResource("/css/match-highlight.css");
        if (MATCH_HIGHLIGHT_CSS == null)
            throw new ExceptionInInitializerError("unable to load match"
                + " highlight CSS file");
    }

    private final AlertFactory alertFactory = new AlertFactory();
    private final GuiTaskRunner taskRunner
        = new GuiTaskRunner("grappa-debugger-%d", Platform::runLater);

    private final ZipTraceDbFactory traceDbFactory
        = new H2TraceDbFactory();

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
        final MainWindowPresenter presenter;

        final JavafxMainWindowView view;
        final Pane pane;

        try {
            view = new JavafxMainWindowView(stage, taskRunner, alertFactory);
        } catch (IOException e) {
            alertFactory.showError("Window creation error",
                "Unable to create window", e);
            return null;
        }

        pane = view.getNode();
        final Scene scene = new Scene(pane, 1024, 768);
        scene.getStylesheets().add(MATCH_HIGHLIGHT_CSS.toExternalForm());
        stage.setScene(scene);
        stage.setTitle("Grappa debugger");

        presenter = new MainWindowPresenter(this, taskRunner, traceDbFactory);
        presenter.setView(view);
        view.getDisplay().setPresenter(presenter);

        windows.put(presenter, stage);

        stage.show();

        return presenter;
    }

    @Override
    public void stop()
    {
        final Set<MainWindowPresenter> set = new HashSet<>(windows.keySet());
        set.forEach(MainWindowPresenter::handleCloseWindow);
        taskRunner.dispose();
    }

    @Override
    public void close(@Nonnull final MainWindowPresenter presenter)
    {
        Objects.requireNonNull(presenter);
        windows.remove(presenter).close();
    }
}
