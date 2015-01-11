package com.github.parboiled1.grappa.debugger;

import com.github.parboiled1.grappa.debugger.mainwindow.DefaultMainWindowView;
import com.github.parboiled1.grappa.debugger.mainwindow.MainWindowPresenter;
import com.github.parboiled1.grappa.debugger.mainwindow.MainWindowUi;
import com.github.parboiled1.grappa.debugger.mainwindow.MainWindowView;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public final class GrappaDebuggerApplication
    extends Application
{
    private Pane pane;

    @Override
    public void start(final Stage primaryStage)
        throws IOException
    {
        final URL url = GrappaDebuggerApplication.class
            .getResource("/mainWindow.fxml");
        if (url == null)
            throw new IOException("cannot load fxml file");

        final FXMLLoader loader = new FXMLLoader(url);

        final Pane pane = loader.load();
        this.pane = pane;

        final MainWindowUi ui = loader.getController();
        final MainWindowView view = new DefaultMainWindowView(ui);
        final MainWindowPresenter presenter = new MainWindowPresenter(ui, view);

        ui.init(presenter, view);

        primaryStage.setScene(new Scene(pane));
        primaryStage.show();
    }

    public static void main(final String... args)
    {
        launch(args);
    }
}
